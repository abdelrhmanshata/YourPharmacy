package com.abdelrhmanshata.yourpharmacy.UI.User;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.abdelrhmanshata.yourpharmacy.Adapter.Adapter_Basket;
import com.abdelrhmanshata.yourpharmacy.Model.ModelItemCart;
import com.abdelrhmanshata.yourpharmacy.R;
import com.abdelrhmanshata.yourpharmacy.databinding.ActivityUShowOrderBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class U_Show_Order_Activity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    public String IDOrder = "";

    ActivityUShowOrderBinding showOrderBinding;

    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference referenceOrders = database.getReference("UserOrders");
    List<ModelItemCart> carts;
    Adapter_Basket adapterBasket;
    SwipeRefreshLayout mSwipeRefreshLayout;

    double totalPrice = 0.0;
    double deliveryPrice = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showOrderBinding = ActivityUShowOrderBinding.inflate(getLayoutInflater());
        setContentView(showOrderBinding.getRoot());

        setSupportActionBar(showOrderBinding.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Shopping Basket");

        IDOrder = getIntent().getStringExtra("IDOrder");

        Initialize_variables();

    }

    public void Initialize_variables() {

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        showOrderBinding.BasketRecyclerview.setHasFixedSize(true);
        showOrderBinding.BasketRecyclerview.setLayoutManager(new LinearLayoutManager(this));

        carts = new ArrayList<>();
        adapterBasket = new Adapter_Basket(this, carts, false);
        showOrderBinding.BasketRecyclerview.setAdapter(adapterBasket);

        loadingAllCarts();
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
                                .child(user.getUid())
                                .child(IDOrder)
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
                        Log.d("U_Show_Order_Activity", error.getMessage());
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