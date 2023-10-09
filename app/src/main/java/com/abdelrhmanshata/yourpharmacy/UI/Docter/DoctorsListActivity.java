package com.abdelrhmanshata.yourpharmacy.UI.Docter;

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
import com.abdelrhmanshata.yourpharmacy.Adapter.Adapter_Users;
import com.abdelrhmanshata.yourpharmacy.Model.ModelDataUser;
import com.abdelrhmanshata.yourpharmacy.R;
import com.abdelrhmanshata.yourpharmacy.databinding.ActivityDoctorsListBinding;

import java.util.ArrayList;
import java.util.List;

public class DoctorsListActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    ActivityDoctorsListBinding doctorsListBinding;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = mAuth.getCurrentUser();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = database.getReference("AllUsers");
    List<ModelDataUser> users;
    Adapter_Users adapterUsers;
    SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        doctorsListBinding = ActivityDoctorsListBinding.inflate(getLayoutInflater());
        setContentView(doctorsListBinding.getRoot());

        Initialize_variables();

    }

    public void Initialize_variables() {
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        doctorsListBinding.UsersRecyclerview.setHasFixedSize(true);
        doctorsListBinding.UsersRecyclerview.setLayoutManager(new LinearLayoutManager(this));

        users = new ArrayList<>();

        adapterUsers = new Adapter_Users(this, users);
        doctorsListBinding.UsersRecyclerview.setAdapter(adapterUsers);

        loadingAllUsers();

    }

    public void loadingAllUsers() {
        databaseReference
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        users.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            ModelDataUser user = snapshot.getValue(ModelDataUser.class);
                            if (user != null)
                                if (!user.getUserID().equals(firebaseUser.getUid()))
                                    if (user.getUserType().toLowerCase().contains("docter"))
                                        users.add(user);
                        }
                        adapterUsers.notifyDataSetChanged();
                        if (users.isEmpty())
                            doctorsListBinding.UsersRecyclerviewImage.setVisibility(View.VISIBLE);
                        else
                            doctorsListBinding.UsersRecyclerviewImage.setVisibility(View.GONE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d("DoctorsListActivity", error.getMessage());
                    }
                });
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(() -> {
            mSwipeRefreshLayout.setRefreshing(false);
            loadingAllUsers();
        }, 1000);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}