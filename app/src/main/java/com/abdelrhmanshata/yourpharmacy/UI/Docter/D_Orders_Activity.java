package com.abdelrhmanshata.yourpharmacy.UI.Docter;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.abdelrhmanshata.yourpharmacy.Adapter.Adapter_Orders;
import com.abdelrhmanshata.yourpharmacy.Model.ModelID;
import com.abdelrhmanshata.yourpharmacy.Model.ModelOrder;
import com.abdelrhmanshata.yourpharmacy.R;
import com.abdelrhmanshata.yourpharmacy.databinding.ActivityDOrdersBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class D_Orders_Activity extends AppCompatActivity
        implements Adapter_Orders.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener {


    ActivityDOrdersBinding ordersBinding;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference referenceOrders = database.getReference("UserOrders");

    List<ModelOrder> orders;
    Adapter_Orders adapterOrders;
    SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ordersBinding = ActivityDOrdersBinding.inflate(getLayoutInflater());
        setContentView(ordersBinding.getRoot());

        setSupportActionBar(ordersBinding.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Orders Page");

        Initialize_variables();

    }

    public void Initialize_variables() {
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        ordersBinding.OrdersRecyclerview.setHasFixedSize(true);
        ordersBinding.OrdersRecyclerview.setLayoutManager(new LinearLayoutManager(this));

        orders = new ArrayList<>();

        adapterOrders = new Adapter_Orders(this, orders);
        ordersBinding.OrdersRecyclerview.setAdapter(adapterOrders);
        adapterOrders.setOnItemClickListener(this);

    }

    public void loadingAllUserID() {
        orders.clear();
        referenceOrders
                .child("ID")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        orders.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            ModelID ID = snapshot.getValue(ModelID.class);
                            if (ID != null) {
                                loadingAllOrders(ID.getID());
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d("D_Orders_Activity", error.getMessage());
                    }
                });
    }

    public void loadingAllOrders(String userID) {
        referenceOrders
                .child("Orders")
                .child(userID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            ModelOrder order = snapshot.getValue(ModelOrder.class);
                            if (order != null)
                                orders.add(order);
                        }
                        adapterOrders.notifyDataSetChanged();
                        if (orders.isEmpty())
                            ordersBinding.OrdersRecyclerviewImage.setVisibility(View.VISIBLE);
                        else
                            ordersBinding.OrdersRecyclerviewImage.setVisibility(View.GONE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d("D_Orders_Activity", error.getMessage());
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        onRefresh();
    }

    @Override
    protected void onPause() {
        super.onPause();
        orders.clear();
        adapterOrders.notifyDataSetChanged();
        if (orders.isEmpty())
            ordersBinding.OrdersRecyclerviewImage.setVisibility(View.VISIBLE);
        else
            ordersBinding.OrdersRecyclerviewImage.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void onItem_Click(int position) {
        ModelOrder order = orders.get(position);
        Intent intent = new Intent(D_Orders_Activity.this, D_Show_Order_Activity.class);
        intent.putExtra("ORDER", order);
        startActivity(intent);
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(() -> {
            mSwipeRefreshLayout.setRefreshing(false);
            loadingAllUserID();
        }, 1000);
    }

}