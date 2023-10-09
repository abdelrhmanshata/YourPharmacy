package com.abdelrhmanshata.yourpharmacy.UI.Docter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.abdelrhmanshata.yourpharmacy.Adapter.Adapter_Medicine;
import com.abdelrhmanshata.yourpharmacy.Model.ModelMedicine;
import com.abdelrhmanshata.yourpharmacy.R;
import com.abdelrhmanshata.yourpharmacy.databinding.ActivityDDiscountsBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class D_Discounts_Activity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    public static String NameSearch = "";
    ActivityDDiscountsBinding discountsBinding;
    ValueEventListener mDBListener;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference mDatabaseRef = database.getReference("Medicines");
    List<ModelMedicine> medicinesList;
    Adapter_Medicine adapterMedicine;
    SwipeRefreshLayout mSwipeRefreshLayout;
    TextWatcher searchTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(final CharSequence s, int start, int before, int count) {
            mDBListener = mDatabaseRef
                    .orderByChild("medicineName")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            medicinesList.clear();
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                ModelMedicine medicine = snapshot.getValue(ModelMedicine.class);
                                if (medicine != null) {
                                    if (medicine.isDiscount())
                                        if (medicine.getMedicineName().toLowerCase().contains(s))
                                            medicinesList.add(medicine);
                                }
                            }
                            adapterMedicine.notifyDataSetChanged();
                            if (medicinesList.isEmpty())
                                discountsBinding.MedicineRecyclerviewImage.setVisibility(View.VISIBLE);
                            else
                                discountsBinding.MedicineRecyclerviewImage.setVisibility(View.GONE);
                            NameSearch = "";
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.d("D_Discounts_Activity", error.getMessage());
                        }
                    });
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        discountsBinding = ActivityDDiscountsBinding.inflate(getLayoutInflater());
        setContentView(discountsBinding.getRoot());

        setSupportActionBar(discountsBinding.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Discounts Page");


        Initialize_variables();

    }

    private void Initialize_variables() {

        mSwipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        medicinesList = new ArrayList<>();

        adapterMedicine = new Adapter_Medicine(this, medicinesList);
        discountsBinding.MedicineGridView.setAdapter(adapterMedicine);

        discountsBinding.MedicineGridView.setOnItemClickListener((parent, view, position, id) -> {
            ModelMedicine medicine = medicinesList.get(position);
            Intent intent = new Intent(D_Discounts_Activity.this, ShowMedicineActivity.class);
            intent.putExtra("MEDICINE", medicine);
            startActivity(intent);
        });

        discountsBinding.MedicineSearch.addTextChangedListener(searchTextWatcher);

        loadingDiscountsMedicine();
    }

    public void loadingDiscountsMedicine() {
        mDBListener = mDatabaseRef
                .orderByChild("medicineName")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        medicinesList.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            ModelMedicine medicine = snapshot.getValue(ModelMedicine.class);
                            if (medicine != null)
                                if (medicine.isDiscount())
                                    medicinesList.add(medicine);
                        }
                        adapterMedicine.notifyDataSetChanged();
                        if (medicinesList.isEmpty())
                            discountsBinding.MedicineRecyclerviewImage.setVisibility(View.VISIBLE);
                        else
                            discountsBinding.MedicineRecyclerviewImage.setVisibility(View.GONE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d("D_Discounts_Activity", error.getMessage());
                    }
                });
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(() -> {
            mSwipeRefreshLayout.setRefreshing(false);
            loadingDiscountsMedicine();
        }, 1000);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onResume() {
        super.onResume();
        adapterMedicine.notifyDataSetChanged();
        discountsBinding.MedicineSearch.setText(NameSearch + "");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDatabaseRef.removeEventListener(mDBListener);
        medicinesList.clear();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}