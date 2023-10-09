package com.abdelrhmanshata.yourpharmacy.UI.Docter;

import android.annotation.SuppressLint;
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
import com.abdelrhmanshata.yourpharmacy.databinding.ActivityDContactsUsBinding;

import java.util.Objects;

import es.dmoral.toasty.Toasty;

public class D_ContactsUSActivity extends AppCompatActivity {

    ActivityDContactsUsBinding contactsUsBinding;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference = database.getReference("Communication");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        contactsUsBinding = ActivityDContactsUsBinding.inflate(getLayoutInflater());
        setContentView(contactsUsBinding.getRoot());

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");


        loadingData();

        contactsUsBinding.Save.setOnClickListener(v -> {

            String PharmacyPhone = contactsUsBinding.pharmacyPhoneNumber.getText().toString().trim();
            String DeliveryPhone = contactsUsBinding.deliveryPhoneNumber.getText().toString().trim();
            String WebSite = contactsUsBinding.webSite.getText().toString().trim();
            String Email = contactsUsBinding.Email.getText().toString().trim();
            String Address = contactsUsBinding.Address.getText().toString().trim();

            UpdataData(PharmacyPhone, DeliveryPhone, WebSite, Email, Address);
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

    private void UpdataData(String... data) {

        reference
                .child("ContactUS")
                .child("PharmacyPhone")
                .setValue(data[0] + "");

        reference
                .child("ContactUS")
                .child("DeliveryPhone")
                .setValue(data[1] + "");

        reference
                .child("ContactUS")
                .child("WebSite")
                .setValue(data[2] + "");


        reference
                .child("ContactUS")
                .child("Email")
                .setValue(data[3] + "");

        reference
                .child("ContactUS")
                .child("Address")
                .setValue(data[4] + "");

        Toasty.success(D_ContactsUSActivity.this, "" + getResources().getString(R.string.updatedSuccessfully), Toast.LENGTH_SHORT).show();
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