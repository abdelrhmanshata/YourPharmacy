package com.abdelrhmanshata.yourpharmacy.UI.User;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.abdelrhmanshata.yourpharmacy.R;
import com.abdelrhmanshata.yourpharmacy.databinding.ActivityUContactsUsBinding;

import java.util.Objects;

import es.dmoral.toasty.Toasty;

public class U_ContactsUSActivity extends AppCompatActivity {

    ActivityUContactsUsBinding contactsUsBinding;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference = database.getReference("Communication");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        contactsUsBinding = ActivityUContactsUsBinding.inflate(getLayoutInflater());
        setContentView(contactsUsBinding.getRoot());

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        loadingData();

        contactsUsBinding
                .PharmacyPhoneLayout
                .setOnClickListener(v -> {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + Uri.encode(contactsUsBinding.pharmacyPhoneNumber.getText().toString())));
                    startActivity(intent);
                });

        contactsUsBinding
                .DeliveryPhoneLayout
                .setOnClickListener(v -> {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + Uri.encode(contactsUsBinding.deliveryPhoneNumber.getText().toString())));
                    startActivity(intent);
                });

        contactsUsBinding
                .WebSiteLayout
                .setOnClickListener(v -> {
                    String url = contactsUsBinding.webSite.getText().toString();
                    try {
                        Uri uri = Uri.parse(url);
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(uri);
                        startActivity(i);
                    } catch (Exception e) {
                        Toasty.error(this, "Error Website", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadingData() {
        reference
                .child("ContactUS")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String PharmacyPhone = snapshot.child("PharmacyPhone").getValue(String.class);
                        String DeliveryPhone = snapshot.child("DeliveryPhone").getValue(String.class);
                        String WebSite = snapshot.child("WebSite").getValue(String.class);
                        String Email = snapshot.child("Email").getValue(String.class);
                        String Address = snapshot.child("Address").getValue(String.class);

                        contactsUsBinding.pharmacyPhoneNumber.setText(PharmacyPhone);
                        contactsUsBinding.deliveryPhoneNumber.setText(DeliveryPhone);
                        contactsUsBinding.webSite.setText(WebSite);
                        contactsUsBinding.Email.setText(Email);
                        contactsUsBinding.Address.setText(Address);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
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