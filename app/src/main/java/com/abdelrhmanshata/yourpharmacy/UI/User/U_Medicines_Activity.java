package com.abdelrhmanshata.yourpharmacy.UI.User;

import android.annotation.SuppressLint;
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
import com.abdelrhmanshata.yourpharmacy.Adapter.Adapter_U_Medicines;
import com.abdelrhmanshata.yourpharmacy.Model.ModelMedicine;
import com.abdelrhmanshata.yourpharmacy.R;
import com.abdelrhmanshata.yourpharmacy.databinding.ActivityUMedicinesBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class U_Medicines_Activity extends AppCompatActivity
        implements SwipeRefreshLayout.OnRefreshListener {

    public static String NameSearch = "";
    ActivityUMedicinesBinding medicinesBinding;
    ValueEventListener mDBListener;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference mDatabaseRef = database.getReference("Medicines");
    List<ModelMedicine> medicinesList;
    Adapter_U_Medicines adapterMedicine;
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
                                    if (medicine.getMedicineName().toLowerCase().contains(s))
                                        medicinesList.add(medicine);
                                }
                            }
                            adapterMedicine.notifyDataSetChanged();
                            if (medicinesList.isEmpty())
                                medicinesBinding.MedicineRecyclerviewImage.setVisibility(View.VISIBLE);
                            else
                                medicinesBinding.MedicineRecyclerviewImage.setVisibility(View.GONE);
                            NameSearch = "";
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.d("D_Medicines_Activity", error.getMessage());
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
        medicinesBinding = ActivityUMedicinesBinding.inflate(getLayoutInflater());
        setContentView(medicinesBinding.getRoot());

        setSupportActionBar(medicinesBinding.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Medicines Page");

        Initialize_variables();

    }

    private void Initialize_variables() {

        mSwipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        medicinesList = new ArrayList<>();

        adapterMedicine = new Adapter_U_Medicines(this, medicinesList);
        medicinesBinding.MedicineGridView.setAdapter(adapterMedicine);

        /*medicinesBinding.MedicineGridView.setOnItemClickListener((parent, view, position, id) -> {
            ModelMedicine medicine = medicinesList.get(position);
            Intent intent = new Intent(U_Medicines_Activity.this, ShowMedicineActivity.class);
            intent.putExtra("MEDICINE", medicine);
            startActivity(intent);
        });*/

        medicinesBinding.MedicineSearch.addTextChangedListener(searchTextWatcher);

        loadingAllMedicine();
    }


    public void loadingAllMedicine() {
        mDBListener = mDatabaseRef
                .orderByChild("medicineName")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        medicinesList.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            ModelMedicine medicine = snapshot.getValue(ModelMedicine.class);
                            if (medicine != null)
                                medicinesList.add(medicine);
                        }
                        adapterMedicine.notifyDataSetChanged();
                        if (medicinesList.isEmpty())
                            medicinesBinding.MedicineRecyclerviewImage.setVisibility(View.VISIBLE);
                        else
                            medicinesBinding.MedicineRecyclerviewImage.setVisibility(View.GONE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d("D_Medicines_Activity", error.getMessage());
                    }
                });
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(() -> {
            mSwipeRefreshLayout.setRefreshing(false);
            loadingAllMedicine();
        }, 1000);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onResume() {
        super.onResume();
        adapterMedicine.notifyDataSetChanged();
        medicinesBinding.MedicineSearch.setText(NameSearch + "");
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