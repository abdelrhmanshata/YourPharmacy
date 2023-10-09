package com.abdelrhmanshata.yourpharmacy.UI.Docter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.abdelrhmanshata.yourpharmacy.Model.ModelMedicine;
import com.abdelrhmanshata.yourpharmacy.R;
import com.abdelrhmanshata.yourpharmacy.UI.ScannerQrCodeActivity;
import com.abdelrhmanshata.yourpharmacy.databinding.ActivityShowMedicineBinding;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.Objects;

import es.dmoral.toasty.Toasty;

public class ShowMedicineActivity extends AppCompatActivity {
    public static String QRCode = "";
    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    private final StorageReference storageReference = storage.getReference("MedicinesImages");
    ActivityShowMedicineBinding showMedicineBinding;
    Toolbar toolbar;
    ModelMedicine medicine;
    Uri filePath = null;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference referenceMedicines = database.getReference("Medicines");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showMedicineBinding = ActivityShowMedicineBinding.inflate(getLayoutInflater());
        setContentView(showMedicineBinding.getRoot());

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Edite Page");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        medicine = (ModelMedicine) getIntent().getSerializableExtra("MEDICINE");
        loadingObject(medicine);

        showMedicineBinding.continueSave.setOnClickListener(v -> EditeMedicine(medicine));

        showMedicineBinding.btnQRCode.setOnClickListener(v -> getQRCode());

        showMedicineBinding.medicamentImage.setOnClickListener(v -> SelectImage());

        showMedicineBinding.btnisDiscount.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                showMedicineBinding.medicamentDiscount.setEnabled(isChecked);
            } else {
                showMedicineBinding.medicamentDiscount.setEnabled(isChecked);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!QRCode.isEmpty())
            showMedicineBinding.medicamentQR.setText(QRCode);
    }

    private void loadingObject(ModelMedicine medicine) {
        try {
            Picasso
                    .get()
                    .load(medicine.getMedicineImage().trim() + "")
                    .fit()
                    .placeholder(R.drawable.loading)
                    .into(showMedicineBinding.medicamentImage);
        } catch (Exception e) {
            Log.d("" + this, e.getMessage());
        }
        showMedicineBinding.medicamentName.setText(medicine.getMedicineName());
        showMedicineBinding.medicamentPrice.setText(String.valueOf(medicine.getMedicinePrice()));
        showMedicineBinding.medicamentQuantity.setText(String.valueOf(medicine.getNumOfMedicine()));
        showMedicineBinding.medicamentQR.setText(medicine.getMedicineQR());
        showMedicineBinding.medicamentDiscount.setText(String.valueOf(medicine.getMedicineDiscount()));
        showMedicineBinding.medicamentDiscount.setEnabled(medicine.isDiscount());
        showMedicineBinding.btnisDiscount.setChecked(medicine.isDiscount());
        if (medicine.getMedicineType().equals("Tablets")) {
            showMedicineBinding.radioGroupType.check(R.id.tablets);
        } else if (medicine.getMedicineType().equals("Drink")) {
            showMedicineBinding.radioGroupType.check(R.id.drink);
        } else {
            showMedicineBinding.radioGroupType.check(R.id.injection);
        }
    }

    @SuppressLint("NonConstantResourceId")
    private void EditeMedicine(ModelMedicine modelMedicine) {
        showMedicineBinding.progressCircle.setVisibility(View.VISIBLE);
        String Name = Objects.requireNonNull(showMedicineBinding.medicamentName.getText()).toString().trim();
        if (Name.isEmpty()) {
            showMedicineBinding.medicamentName.setError(getString(R.string.NameIsRequired));
            showMedicineBinding.medicamentName.setFocusable(true);
            showMedicineBinding.medicamentName.requestFocus();
            showMedicineBinding.progressCircle.setVisibility(View.GONE);
            return;
        }
        modelMedicine.setMedicineName(Name);

        String Price = Objects.requireNonNull(showMedicineBinding.medicamentPrice.getText()).toString().trim();
        if (Price.isEmpty()) {
            showMedicineBinding.medicamentPrice.setError(getString(R.string.NameIsRequired));
            showMedicineBinding.medicamentPrice.setFocusable(true);
            showMedicineBinding.medicamentPrice.requestFocus();
            showMedicineBinding.progressCircle.setVisibility(View.GONE);
            return;
        }
        modelMedicine.setMedicinePrice(Double.parseDouble(Price));

        String Quantity = Objects.requireNonNull(showMedicineBinding.medicamentQuantity.getText()).toString().trim();
        if (Quantity.isEmpty()) {
            showMedicineBinding.medicamentQuantity.setError(getString(R.string.QuantityIsRequired));
            showMedicineBinding.medicamentQuantity.setFocusable(true);
            showMedicineBinding.medicamentQuantity.requestFocus();
            showMedicineBinding.progressCircle.setVisibility(View.GONE);
            return;
        }
        modelMedicine.setNumOfMedicine(Integer.parseInt(Quantity));

        String Type = "";
        RadioButton selectedType;
        if (showMedicineBinding.radioGroupType.getCheckedRadioButtonId() == -1) {
            Toasty.info(ShowMedicineActivity.this, getString(R.string.choose_type_Medicine), Toast.LENGTH_SHORT).show();
            showMedicineBinding.progressCircle.setVisibility(View.GONE);
            return;
        } else {
            selectedType = findViewById(showMedicineBinding.radioGroupType.getCheckedRadioButtonId());
            switch (selectedType.getId()) {
                case R.id.tablets:
                    Type = "Tablets";
                    break;
                case R.id.drink:
                    Type = "Drink";
                    break;
                case R.id.injection:
                    Type = "Injection";
                    break;
            }
        }
        modelMedicine.setMedicineType(Type);

        String QR = Objects.requireNonNull(showMedicineBinding.medicamentQR.getText()).toString().trim();
        if (QR.isEmpty()) {
            showMedicineBinding.medicamentQR.setError(getString(R.string.QRIsRequired));
            showMedicineBinding.medicamentQR.setFocusable(true);
            showMedicineBinding.medicamentQR.requestFocus();
            showMedicineBinding.progressCircle.setVisibility(View.GONE);
            return;
        }
        modelMedicine.setMedicineQR(QR);

        boolean isDiscount = showMedicineBinding.btnisDiscount.isChecked();
        modelMedicine.setDiscount(isDiscount);

        String Discount = Objects.requireNonNull(showMedicineBinding.medicamentDiscount.getText()).toString().trim();
        if (isDiscount) {
            if (Discount.isEmpty()) {
                showMedicineBinding.medicamentDiscount.setError(getString(R.string.DiscountIsRequired));
                showMedicineBinding.medicamentDiscount.setFocusable(true);
                showMedicineBinding.medicamentDiscount.requestFocus();
                showMedicineBinding.progressCircle.setVisibility(View.GONE);
                return;
            }
            modelMedicine.setMedicineDiscount(Double.parseDouble(Discount));
        }

        if (filePath == null) {
            modelMedicine.setMedicineImage(medicine.getMedicineImage());
            referenceMedicines.child(medicine.getMedicineID()).setValue(modelMedicine).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    showMedicineBinding.progressCircle.setVisibility(View.GONE);
                    Toasty.success(this, getResources().getString(R.string.Added_successfully), Toast.LENGTH_SHORT).show();
                    onBackPressed();
                }
            });
        } else {
            uploadImageSelect(filePath, modelMedicine);
        }
    }

    private void SelectImage() {
        CropImage.activity()
                //.setGuidelines(CropImageView.Guidelines.ON)
                .start(ShowMedicineActivity.this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                filePath = result.getUri();
                try {
                    Picasso
                            .get()
                            .load(filePath)
                            .fit()
                            .placeholder(R.drawable.loading)
                            .into(showMedicineBinding.medicamentImage);
                    showMedicineBinding.medicamentImage.setScaleType(ImageView.ScaleType.FIT_XY);
                } catch (Exception e) {
                    Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Log.d("Error", error.getMessage());
            }
        }
    }

    @SuppressLint("CheckResult")
    private void uploadImageSelect(Uri fileImagePath, ModelMedicine medicine) {
        showMedicineBinding.progressCircle.setVisibility(View.VISIBLE);
        StorageReference ref_Storage;
        UploadTask ref_Storage_image;
        ref_Storage = storageReference.child(medicine.getMedicineID() + "/" + "Medicine.jpg");
        ref_Storage_image = ref_Storage.putFile(fileImagePath);
        ref_Storage_image.addOnSuccessListener(taskSnapshot -> {
            ref_Storage
                    .getDownloadUrl()
                    .addOnSuccessListener(uri -> {
                        medicine.setMedicineImage(uri.toString());
                        referenceMedicines
                                .child(medicine.getMedicineID())
                                .setValue(medicine)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        showMedicineBinding.progressCircle.setVisibility(View.GONE);
                                        Toasty.success(this, getResources().getString(R.string.Added_successfully), Toast.LENGTH_SHORT).show();
                                        onBackPressed();
                                    }
                                });
                    });
        });
    }

    private void getQRCode() {
        Intent intent
                = new Intent(ShowMedicineActivity.this, ScannerQrCodeActivity.class);
        intent.putExtra("Edite_Medicine", true);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}