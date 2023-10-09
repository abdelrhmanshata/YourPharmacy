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
import com.abdelrhmanshata.yourpharmacy.databinding.ActivityDNeedsBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class D_Needs_Activity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    public static String NameSearch = "";
    ActivityDNeedsBinding needsBinding;
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
                                    if (medicine.getNumOfMedicine() <= 0)
                                        if (medicine.getMedicineName().toLowerCase().contains(s))
                                            medicinesList.add(medicine);
                                }
                            }
                            adapterMedicine.notifyDataSetChanged();
                            if (medicinesList.isEmpty())
                                needsBinding.MedicineRecyclerviewImage.setVisibility(View.VISIBLE);
                            else
                                needsBinding.MedicineRecyclerviewImage.setVisibility(View.GONE);
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
        needsBinding = ActivityDNeedsBinding.inflate(getLayoutInflater());
        setContentView(needsBinding.getRoot());

        setSupportActionBar(needsBinding.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Needs Page");

        Initialize_variables();

    }

    private void Initialize_variables() {

        mSwipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        medicinesList = new ArrayList<>();

        adapterMedicine = new Adapter_Medicine(this, medicinesList);
        needsBinding.MedicineGridView.setAdapter(adapterMedicine);

        needsBinding.MedicineGridView.setOnItemClickListener((parent, view, position, id) -> {
            ModelMedicine medicine = medicinesList.get(position);
            Intent intent = new Intent(D_Needs_Activity.this, ShowMedicineActivity.class);
            intent.putExtra("MEDICINE", medicine);
            startActivity(intent);
        });

        needsBinding.MedicineSearch.addTextChangedListener(searchTextWatcher);

        loadingNeedsMedicine();
    }

    public void loadingNeedsMedicine() {
        mDBListener = mDatabaseRef
                .orderByChild("medicineName")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        medicinesList.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            ModelMedicine medicine = snapshot.getValue(ModelMedicine.class);
                            if (medicine != null)
                                if (medicine.getNumOfMedicine() <= 0)
                                    medicinesList.add(medicine);
                        }
                        adapterMedicine.notifyDataSetChanged();
                        if (medicinesList.isEmpty())
                            needsBinding.MedicineRecyclerviewImage.setVisibility(View.VISIBLE);
                        else
                            needsBinding.MedicineRecyclerviewImage.setVisibility(View.GONE);
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
            loadingNeedsMedicine();
        }, 1000);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onResume() {
        super.onResume();
        adapterMedicine.notifyDataSetChanged();
        needsBinding.MedicineSearch.setText(NameSearch + "");
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