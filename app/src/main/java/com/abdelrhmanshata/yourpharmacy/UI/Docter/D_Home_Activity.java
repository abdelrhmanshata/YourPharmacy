package com.abdelrhmanshata.yourpharmacy.UI.Docter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.abdelrhmanshata.yourpharmacy.Adapter.SliderAdapter;
import com.abdelrhmanshata.yourpharmacy.UI.Login_Register.LoginActivity;
import com.abdelrhmanshata.yourpharmacy.Model.ModelDataUser;
import com.abdelrhmanshata.yourpharmacy.R;
import com.abdelrhmanshata.yourpharmacy.databinding.ActivityDHomeBinding;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class D_Home_Activity extends AppCompatActivity {

    ActivityDHomeBinding homeBinding;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference referenceOrders = database.getReference("UserOrders");
    DatabaseReference referenceUsers = database.getReference("AllUsers");

    DrawerLayout drawerLayout;
    Toolbar toolbar;
    NavigationView navigationView;
    ImageView UserImage;
    TextView UserName, UserEmail;
    ActionBarDrawerToggle actionBarDrawerToggle;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        homeBinding = ActivityDHomeBinding.inflate(getLayoutInflater());
        setContentView(homeBinding.getRoot());

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Docter Home Page");
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.activityDocter);
        navigationView = findViewById(R.id.nav_view);
        View headerLayout = navigationView.getHeaderView(0);
        UserImage = headerLayout.findViewById(R.id.userImage);
        UserName = headerLayout.findViewById(R.id.userName);
        UserEmail = headerLayout.findViewById(R.id.userEmail);

        List<Integer> imageList = new ArrayList<>();

        imageList.add(R.drawable.image_docter);
        imageList.add(R.drawable.image_discount1);
        imageList.add(R.drawable.image_online);
        imageList.add(R.drawable.image_orders);
        imageList.add(R.drawable.image_invoices);

        SliderAdapter sliderAdapter = new SliderAdapter(imageList);
        homeBinding.imageSlider.setSliderAdapter(sliderAdapter);
        homeBinding.imageSlider.setAutoCycle(true);
        homeBinding.imageSlider.setSliderTransformAnimation(SliderAnimations.ZOOMOUTTRANSFORMATION);
        homeBinding.imageSlider.setIndicatorAnimation(IndicatorAnimationType.WORM);


        navigationView.setNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.timeWork:
                    startActivity(new Intent(D_Home_Activity.this, D_TimeWorkActivity.class));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    break;

                case R.id.contactsUS:
                    startActivity(new Intent(D_Home_Activity.this, D_ContactsUSActivity.class));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    break;

                case R.id.logout:
                    auth.signOut();
                    startActivity(new Intent(D_Home_Activity.this, LoginActivity.class));
                    finish();
                    break;
            }
            return true;
        });
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.Open, R.string.Close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        loadInfoUser();


        homeBinding.Profile.setOnClickListener(v -> {
            startActivity(new Intent(D_Home_Activity.this, D_Profile_Activity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        homeBinding.Medicines.setOnClickListener(v -> {
            startActivity(new Intent(D_Home_Activity.this, D_Medicines_Activity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        homeBinding.Orders.setOnClickListener(v -> {
            startActivity(new Intent(D_Home_Activity.this, D_Orders_Activity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        homeBinding.Chat.setOnClickListener(v -> {
            startActivity(new Intent(D_Home_Activity.this, D_Chat_Activity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        homeBinding.Discounts.setOnClickListener(v -> {
            startActivity(new Intent(D_Home_Activity.this, D_Discounts_Activity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        homeBinding.Needs.setOnClickListener(v -> {
            startActivity(new Intent(D_Home_Activity.this, D_Needs_Activity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        homeBinding.Delivery.setOnClickListener(v -> {
            showDialogDeliveryPrice();
        });

        homeBinding.DocterList.setOnClickListener(v -> {
            startActivity(new Intent(D_Home_Activity.this, DoctorsListActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadInfoUser();
    }

    public void loadInfoUser() {
        referenceUsers.child(user.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ModelDataUser user = snapshot.getValue(ModelDataUser.class);
                        if (user != null) {
                            Picasso.get().load(user.getUserImageUri().trim())
                                    .into(UserImage);
                            UserName.setText(user.getUserName());
                            UserEmail.setText(user.getUserEmail());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d("DHomeActivity", error.getMessage());
                    }
                });
    }

    public void showDialogDeliveryPrice() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.add_deliveryprice_layout, null);
        dialogBuilder.setView(dialogView);
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        TextInputEditText price = dialogView.findViewById(R.id.Price);
        RelativeLayout Save = dialogView.findViewById(R.id.Save);

        Save.setOnClickListener(v -> {
            if (!price.getText().toString().isEmpty()) {
                referenceOrders
                        .child("DeliveryPrice")
                        .setValue(Double.parseDouble(price.getText().toString()));
                alertDialog.dismiss();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }


}