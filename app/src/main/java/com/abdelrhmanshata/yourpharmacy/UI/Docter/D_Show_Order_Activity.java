package com.abdelrhmanshata.yourpharmacy.UI.Docter;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.GradientDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.abdelrhmanshata.yourpharmacy.Adapter.Adapter_Basket;
import com.abdelrhmanshata.yourpharmacy.Model.ModelItemCart;
import com.abdelrhmanshata.yourpharmacy.Model.ModelMedicine;
import com.abdelrhmanshata.yourpharmacy.Model.ModelOrder;
import com.abdelrhmanshata.yourpharmacy.R;
import com.abdelrhmanshata.yourpharmacy.databinding.ActivityDShowOrderBinding;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class D_Show_Order_Activity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    ActivityDShowOrderBinding showOrderBinding;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference referenceOrders = database.getReference("UserOrders");
    DatabaseReference mDatabaseRef = database.getReference("Medicines");
    List<ModelItemCart> carts;
    Adapter_Basket adapterBasket;
    SwipeRefreshLayout mSwipeRefreshLayout;

    double totalPrice = 0.0;
    double deliveryPrice = 0.0;

    ModelOrder order;

    FusedLocationProviderClient locationProviderClient;

    GradientDrawable colorCircle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showOrderBinding = ActivityDShowOrderBinding.inflate(getLayoutInflater());
        setContentView(showOrderBinding.getRoot());

        order = (ModelOrder) getIntent().getSerializableExtra("ORDER");
        Initialize_variables();

        showOrderBinding.userPhone.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + Uri.encode(order.getUserPhone())));
            startActivity(intent);
        });

        showOrderBinding.userAddress.setOnClickListener(v -> {
            getLocation();
        });

        showOrderBinding
                .InProcessTV
                .setOnClickListener(v -> {
                    referenceOrders
                            .child("Orders")
                            .child(order.getUserID())
                            .child(order.getID())
                            .child("inProcess")
                            .setValue(true);

                    showOrderBinding.InProcessTV.setText("1");
                    colorCircle = (GradientDrawable) showOrderBinding.InProcessTV.getBackground();
                    colorCircle.setColor(getIdColor(true));

                    UpdataMedicinesData();

                });

        showOrderBinding
                .InRoadTV
                .setOnClickListener(v -> {
                    referenceOrders
                            .child("Orders")
                            .child(order.getUserID())
                            .child(order.getID())
                            .child("inRoad")
                            .setValue(true);

                    showOrderBinding.InRoadTV.setText("2");
                    colorCircle = (GradientDrawable) showOrderBinding.InRoadTV.getBackground();
                    colorCircle.setColor(getIdColor(true));
                    showOrderBinding.viewPR.setBackgroundColor(getIdColor(true));


                });

        showOrderBinding
                .DeliveredTV
                .setOnClickListener(v -> {
                    referenceOrders
                            .child("Orders")
                            .child(order.getUserID())
                            .child(order.getID())
                            .child("delivered")
                            .setValue(true);

                    showOrderBinding.DeliveredTV.setText("3");
                    colorCircle = (GradientDrawable) showOrderBinding.DeliveredTV.getBackground();
                    colorCircle.setColor(getIdColor(true));
                    showOrderBinding.viewRD.setBackgroundColor(getIdColor(true));

                });

    }

    public void Initialize_variables() {

        locationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        showOrderBinding.BasketRecyclerview.setHasFixedSize(true);
        showOrderBinding.BasketRecyclerview.setLayoutManager(new LinearLayoutManager(this));

        carts = new ArrayList<>();
        adapterBasket = new Adapter_Basket(this, carts, false);
        showOrderBinding.BasketRecyclerview.setAdapter(adapterBasket);

        showOrderBinding.userName.setText(order.getUserName());
        showOrderBinding.userPhone.setText(order.getUserPhone());
        showOrderBinding.userAddress.setText(order.getUserAddress());

        showOrderBinding.InProcessTV.setText("1");
        colorCircle = (GradientDrawable) showOrderBinding.InProcessTV.getBackground();
        colorCircle.setColor(getIdColor(order.isInProcess()));

        showOrderBinding.InRoadTV.setText("2");
        colorCircle = (GradientDrawable) showOrderBinding.InRoadTV.getBackground();
        colorCircle.setColor(getIdColor(order.isInRoad()));
        showOrderBinding.viewPR.setBackgroundColor(getIdColor(order.isInRoad()));

        showOrderBinding.DeliveredTV.setText("3");
        colorCircle = (GradientDrawable) showOrderBinding.DeliveredTV.getBackground();
        colorCircle.setColor(getIdColor(order.isDelivered()));
        showOrderBinding.viewRD.setBackgroundColor(getIdColor(order.isDelivered()));

        loadingAllCarts();
    }

    private int getIdColor(boolean isDone) {
        if (isDone)
            return ContextCompat.getColor(getApplicationContext(), R.color.colorLime);
        else
            return ContextCompat.getColor(getApplicationContext(), R.color.colorGray);
    }

    public void loadingAllCarts() {
        referenceOrders
                .child("DeliveryPrice")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        deliveryPrice = snapshot.getValue(Double.class);
                        referenceOrders
                                .child("Orders")
                                .child(order.getUserID())
                                .child(order.getID())
                                .child("Order")
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        carts.clear();
                                        totalPrice = 0.0;
                                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                            ModelItemCart question = snapshot.getValue(ModelItemCart.class);
                                            if (question != null) {
                                                carts.add(question);
                                                totalPrice += question.getTotalPrice();
                                            }
                                        }
                                        adapterBasket.notifyDataSetChanged();
                                        if (carts.isEmpty())
                                            showOrderBinding.BasketRecyclerviewImage.setVisibility(View.VISIBLE);
                                        else {
                                            showOrderBinding.BasketRecyclerviewImage.setVisibility(View.GONE);
                                            showOrderBinding.layoutckeckout.setVisibility(View.VISIBLE);
                                            showOrderBinding.medicinePrice.setText(String.valueOf(totalPrice) + " " + getResources().getString(R.string.currency));
                                            showOrderBinding.deliveryPrice.setText(String.valueOf(deliveryPrice) + " " + getResources().getString(R.string.currency));
                                            showOrderBinding.totalPrice.setText(String.valueOf(totalPrice + deliveryPrice) + " " + getResources().getString(R.string.currency));
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Log.d("U_Show_Order_Activity", error.getMessage());
                                    }
                                });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(D_Show_Order_Activity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
            return;
        }
        locationProviderClient
                .getLastLocation()
                .addOnCompleteListener(task -> {
                    Location location = task.getResult();
                    if (location != null) {
                        try {
                            Geocoder geocoder = new Geocoder(D_Show_Order_Activity.this, Locale.getDefault());
                            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                            String uri = "http://maps.google.com/maps?f=d&hl=en&saddr=" + addresses.get(0).getLatitude() + "," + addresses.get(0).getLongitude() + "&daddr=" + order.getLatitude() + "," + order.getLongitude();
                            Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri));
                            startActivity(intent);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    public void UpdataMedicinesData() {
        for (ModelItemCart itemCart : carts) {
            updataSingleMedicine(itemCart);
        }
    }

    public void updataSingleMedicine(ModelItemCart itemCart) {
        mDatabaseRef
                .child(itemCart.getID())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ModelMedicine medicine = dataSnapshot.getValue(ModelMedicine.class);
                        if (medicine != null) {
                            medicine.setNumOfMedicine(medicine.getNumOfMedicine() - itemCart.getNumOfMedicine());
                            mDatabaseRef.child(medicine.getMedicineID()).setValue(medicine);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d("D_Medicines_Activity", error.getMessage());
                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(() -> {
            mSwipeRefreshLayout.setRefreshing(false);
            loadingAllCarts();
        }, 1000);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}