package com.abdelrhmanshata.yourpharmacy.UI.User;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
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
import com.abdelrhmanshata.yourpharmacy.Adapter.Adapter_U_Orders;
import com.abdelrhmanshata.yourpharmacy.Model.ModelOrder;
import com.abdelrhmanshata.yourpharmacy.R;
import com.abdelrhmanshata.yourpharmacy.databinding.ActivityUOrdersBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class U_Orders_Activity extends AppCompatActivity implements Adapter_U_Orders.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener {

    ActivityUOrdersBinding ordersBinding;

    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference referenceOrders = database.getReference("UserOrders");
    ValueEventListener mDBListener;
    List<ModelOrder> orders;
    Adapter_U_Orders adapterUOrders;
    SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ordersBinding = ActivityUOrdersBinding.inflate(getLayoutInflater());
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

        adapterUOrders = new Adapter_U_Orders(this, orders);
        ordersBinding.OrdersRecyclerview.setAdapter(adapterUOrders);
        adapterUOrders.setOnItemClickListener(this);

        loadingAllOrders();

    }

    public void loadingAllOrders() {
        mDBListener = referenceOrders
                .child("Orders")
                .child(user.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        orders.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            ModelOrder order = snapshot.getValue(ModelOrder.class);
                            if (order != null)
                                orders.add(order);
                        }
                        adapterUOrders.notifyDataSetChanged();
                        if (orders.isEmpty())
                            ordersBinding.OrdersRecyclerviewImage.setVisibility(View.VISIBLE);
                        else
                            ordersBinding.OrdersRecyclerviewImage.setVisibility(View.GONE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d("U_Orders_Activity", error.getMessage());
                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void onItem_Click(int position) {
        ModelOrder order = orders.get(position);
        Intent intent = new Intent(U_Orders_Activity.this, U_Show_Order_Activity.class);
        intent.putExtra("IDOrder", order.getID());
        startActivity(intent);
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(() -> {
            mSwipeRefreshLayout.setRefreshing(false);
            loadingAllOrders();
        }, 1000);
    }

}