package com.abdelrhmanshata.yourpharmacy.UI.User;

import android.Manifest;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
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
import com.abdelrhmanshata.yourpharmacy.databinding.ActivityUHomeBinding;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class U_Home_Activity extends AppCompatActivity {

    ActivityUHomeBinding homeBinding;

    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference referenceUsers = database.getReference("AllUsers");

    DrawerLayout drawerLayout;
    Toolbar toolbar;
    NavigationView navigationView;
    ImageView UserImage;
    TextView UserName, UserEmail;
    ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        homeBinding = ActivityUHomeBinding.inflate(getLayoutInflater());
        setContentView(homeBinding.getRoot());

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("User Home Page");
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.activityUser);
        navigationView = findViewById(R.id.nav_view);
        View headerLayout = navigationView.getHeaderView(0);
        UserImage = headerLayout.findViewById(R.id.userImage);
        UserName = headerLayout.findViewById(R.id.userName);
        UserEmail = headerLayout.findViewById(R.id.userEmail);

        ActivityCompat.requestPermissions(U_Home_Activity.this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);

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
                    startActivity(new Intent(U_Home_Activity.this, U_TimeWorkActivity.class));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    break;

                case R.id.contactsUS:
                    startActivity(new Intent(U_Home_Activity.this, U_ContactsUSActivity.class));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    break;

                case R.id.logout:
                    auth.signOut();
                    startActivity(new Intent(U_Home_Activity.this, LoginActivity.class));
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
            startActivity(new Intent(U_Home_Activity.this, U_Profile_Activity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        homeBinding.Medicines.setOnClickListener(v -> {
            startActivity(new Intent(U_Home_Activity.this, U_Medicines_Activity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        homeBinding.Orders.setOnClickListener(v -> {
            startActivity(new Intent(U_Home_Activity.this, U_Orders_Activity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        homeBinding.Chat.setOnClickListener(v -> {
            startActivity(new Intent(U_Home_Activity.this, U_Chat_Activity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        homeBinding.Discounts.setOnClickListener(v -> {
            startActivity(new Intent(U_Home_Activity.this, U_Discounts_Activity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        homeBinding.shoppingBasket.setOnClickListener(v -> {
            startActivity(new Intent(U_Home_Activity.this, ShoppingBasketActivity.class));
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
                        Log.d("UHomeActivity", error.getMessage());
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