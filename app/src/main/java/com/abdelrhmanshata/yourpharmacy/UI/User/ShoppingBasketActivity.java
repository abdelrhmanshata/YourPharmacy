package com.abdelrhmanshata.yourpharmacy.UI.User;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.braintreepayments.cardform.view.CardForm;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.abdelrhmanshata.yourpharmacy.Adapter.Adapter_Basket;
import com.abdelrhmanshata.yourpharmacy.Model.ModelDataUser;
import com.abdelrhmanshata.yourpharmacy.Model.ModelID;
import com.abdelrhmanshata.yourpharmacy.Model.ModelItemCart;
import com.abdelrhmanshata.yourpharmacy.Model.ModelLocation;
import com.abdelrhmanshata.yourpharmacy.Model.ModelOrder;
import com.abdelrhmanshata.yourpharmacy.R;
import com.abdelrhmanshata.yourpharmacy.databinding.ActivityShoppingBasketBinding;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import es.dmoral.toasty.Toasty;

public class ShoppingBasketActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    ActivityShoppingBasketBinding basketBinding;

    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference referenceUsers = database.getReference("AllUsers");
    DatabaseReference referenceBasket = database.getReference("Basket");
    DatabaseReference referenceOrders = database.getReference("UserOrders");
    List<ModelItemCart> carts;
    Adapter_Basket adapterBasket;
    SwipeRefreshLayout mSwipeRefreshLayout;

    ModelDataUser dataUser = new ModelDataUser();

    double totalPrice = 0.0;
    double deliveryPrice = 0.0;

    FusedLocationProviderClient locationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        basketBinding = ActivityShoppingBasketBinding.inflate(getLayoutInflater());
        setContentView(basketBinding.getRoot());

        setSupportActionBar(basketBinding.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Shopping Basket");

        Initialize_variables();

        basketBinding.checkOut.setOnClickListener(v -> showDialogpayment());
    }

    public void Initialize_variables() {

        locationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        basketBinding.BasketRecyclerview.setHasFixedSize(true);
        basketBinding.BasketRecyclerview.setLayoutManager(new LinearLayoutManager(this));

        carts = new ArrayList<>();
        adapterBasket = new Adapter_Basket(this, carts, true);
        basketBinding.BasketRecyclerview.setAdapter(adapterBasket);

        loadingUserData();
        loadingAllCarts();
    }

    public void loadingAllCarts() {
        referenceOrders
                .child("DeliveryPrice")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        deliveryPrice = snapshot.getValue(Double.class);
                        referenceBasket
                                .child(user.getUid())
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
                                            basketBinding.BasketRecyclerviewImage.setVisibility(View.VISIBLE);
                                        else {
                                            basketBinding.BasketRecyclerviewImage.setVisibility(View.GONE);
                                            basketBinding.layoutckeckout.setVisibility(View.VISIBLE);
                                            basketBinding.medicinePrice.setText(String.valueOf(totalPrice) + " " + getResources().getString(R.string.currency));
                                            basketBinding.deliveryPrice.setText(String.valueOf(deliveryPrice) + " " + getResources().getString(R.string.currency));
                                            basketBinding.totalPrice.setText(String.valueOf(totalPrice + deliveryPrice) + " " + getResources().getString(R.string.currency));
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Log.d("ShoppingBasketActivity", error.getMessage());
                                    }
                                });
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public void showDialogpayment() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.payment_layout, null);
        dialogBuilder.setView(dialogView);
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        RelativeLayout cashLayout = dialogView.findViewById(R.id.cashLayout);
        RelativeLayout onlineLayout = dialogView.findViewById(R.id.onlineLayout);

        cashLayout.setOnClickListener(v -> {
            getLocation("Cash");
            alertDialog.dismiss();
        });

        onlineLayout.setOnClickListener(v -> {
            showDialogCreditCard();
            alertDialog.dismiss();
        });
    }

    public void showDialogCreditCard() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.creditcard_layout, null);
        dialogBuilder.setView(dialogView);
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
        CardForm cardForm = dialogView.findViewById(R.id.card_form);
        cardForm.cardRequired(true)
                .expirationRequired(true)
                .cvvRequired(true)
                .postalCodeRequired(true)
                .mobileNumberRequired(true)
                .mobileNumberExplanation("SMS is required on this number")
                .setup(ShoppingBasketActivity.this);
        cardForm.getCvvEditText().setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);

        RelativeLayout buy = dialogView.findViewById(R.id.buy);
        ProgressBar progressBar = dialogView.findViewById(R.id.progress);
        buy.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            if (cardForm.isValid()) {
                getLocation("Online");
                progressBar.setVisibility(View.GONE);
                alertDialog.dismiss();
            } else {
                Toasty.error(this, "Error Card Form", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getLocation(String typePayment) {
        basketBinding.progressCheckOut.setVisibility(View.VISIBLE);
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(ShoppingBasketActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
            basketBinding.progressCheckOut.setVisibility(View.GONE);
            return;
        }
        locationProviderClient
                .getLastLocation()
                .addOnCompleteListener(task -> {
                    Location location = task.getResult();
                    if (location != null) {
                        try {
                            Geocoder geocoder = new Geocoder(ShoppingBasketActivity.this, Locale.getDefault());
                            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                            ModelLocation location1 = new ModelLocation();
                            location1.setLatitude(String.valueOf(addresses.get(0).getLatitude()));
                            location1.setLongitude(String.valueOf(addresses.get(0).getLongitude()));
                            location1.setCountryName(String.valueOf(addresses.get(0).getCountryName()));
                            location1.setLocality(String.valueOf(addresses.get(0).getLocality()));
                            location1.setAddress(addresses.get(0).getAddressLine(0));
                            basketBinding.progressCheckOut.setVisibility(View.GONE);
                            addOrder(location1, typePayment);
                        } catch (IOException e) {
                            e.printStackTrace();
                            basketBinding.progressCheckOut.setVisibility(View.GONE);
                        }
                    }else{
                        basketBinding.progressCheckOut.setVisibility(View.GONE);
                        Toasty.info(this, "Please activate the GPS", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadingUserData() {
        referenceUsers
                .child(user.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ModelDataUser modelDataUser = snapshot.getValue(ModelDataUser.class);
                        if (modelDataUser != null) {
                            dataUser = modelDataUser;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d("ShoppingBasketActivity", error.getMessage());
                    }
                });
    }

    private void addOrder(ModelLocation location, String typePayment) {
        basketBinding.progressCheckOut.setVisibility(View.VISIBLE);
        String ID = referenceOrders.push().getKey();
        ModelOrder order = new ModelOrder();
        order.setID(ID);
        order.setUserID(dataUser.getUserID());
        order.setUserName(dataUser.getUserName());
        order.setUserPhone(dataUser.getUserPhone());
        order.setUserAddress(location.getAddress());
        order.setLatitude(location.getLatitude());
        order.setLongitude(location.getLongitude());
        order.setDate(getCurrentDate());
        order.setTime(getCurrentTime());
        order.setTypePayment(typePayment);
        order.setInProcess(false);
        order.setInRoad(false);
        order.setDelivered(false);

        ModelID id = new ModelID(dataUser.getUserID());
        referenceOrders
                .child("ID")
                .child(dataUser.getUserID())
                .setValue(id);

        referenceOrders
                .child("Orders")
                .child(dataUser.getUserID())
                .child(ID)
                .setValue(order)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (ModelItemCart itemCart : carts) {
                            referenceOrders
                                    .child("Orders")
                                    .child(dataUser.getUserID())
                                    .child(ID)
                                    .child("Order")
                                    .child(itemCart.getID())
                                    .setValue(itemCart);
                        }
                        basketBinding.progressCheckOut.setVisibility(View.GONE);
                        basketBinding.layoutckeckout.setVisibility(View.GONE);
                        referenceBasket
                                .child(user.getUid())
                                .removeValue().addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful())
                                Toasty.success(ShoppingBasketActivity.this, getResources().getString(R.string.Added_successfully), Toast.LENGTH_SHORT).show();
                        });
                    }
                });
    }

    public String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("yyyy/M/dd", Locale.ENGLISH);
        return mdformat.format(calendar.getTime());
    }

    public String getCurrentTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
        return mdformat.format(calendar.getTime());
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