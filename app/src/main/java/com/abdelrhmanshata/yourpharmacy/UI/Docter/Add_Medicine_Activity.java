package com.abdelrhmanshata.yourpharmacy.UI.Docter;

import android.Manifest;
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
import androidx.core.app.ActivityCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.abdelrhmanshata.yourpharmacy.Model.ModelMedicine;
import com.abdelrhmanshata.yourpharmacy.R;
import com.abdelrhmanshata.yourpharmacy.UI.ScannerQrCodeActivity;
import com.abdelrhmanshata.yourpharmacy.databinding.ActivityAddMedicineBinding;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.Objects;

import es.dmoral.toasty.Toasty;

public class Add_Medicine_Activity extends AppCompatActivity {

    public static String QRCode = "";
    ActivityAddMedicineBinding addMedicineBinding;
    Uri filePath = null;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference referenceMedicines = database.getReference("Medicines");
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageReference = storage.getReference("MedicinesImages");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addMedicineBinding = ActivityAddMedicineBinding.inflate(getLayoutInflater());
        setContentView(addMedicineBinding.getRoot());

        setSupportActionBar(addMedicineBinding.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        // Permissions to open Camera
        ActivityCompat.requestPermissions(Add_Medicine_Activity.this,
                new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                1);

        addMedicineBinding.medicamentQR.setText(QRCode);

        addMedicineBinding.btnQRCode.setOnClickListener(v -> {
            getQRCode();
        });

        addMedicineBinding.medicamentImage.setOnClickListener(v -> {
            SelectImage();
        });
        addMedicineBinding.continueAdd.setOnClickListener(v -> AddNewMedicine());

        addMedicineBinding.btnisDiscount.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if (isChecked) {
                addMedicineBinding.medicamentDiscount.setEnabled(isChecked);
            } else {
                addMedicineBinding.medicamentDiscount.setEnabled(isChecked);
            }
        });
    }

    private void getQRCode() {
        Intent intent
                = new Intent(Add_Medicine_Activity.this, ScannerQrCodeActivity.class);
        intent.putExtra("ADD_Medicine", true);
        startActivity(intent);
    }

    @SuppressLint("NonConstantResourceId")
    private void AddNewMedicine() {
        addMedicineBinding.progressCircle.setVisibility(View.VISIBLE);
        String Name = Objects.requireNonNull(addMedicineBinding.medicamentName.getText()).toString().trim();
        if (Name.isEmpty()) {
            addMedicineBinding.medicamentName.setError(getString(R.string.NameIsRequired));
            addMedicineBinding.medicamentName.setFocusable(true);
            addMedicineBinding.medicamentName.requestFocus();
            addMedicineBinding.progressCircle.setVisibility(View.GONE);
            return;
        }
        String Price = Objects.requireNonNull(addMedicineBinding.medicamentPrice.getText()).toString().trim();
        if (Price.isEmpty()) {
            addMedicineBinding.medicamentPrice.setError(getString(R.string.NameIsRequired));
            addMedicineBinding.medicamentPrice.setFocusable(true);
            addMedicineBinding.medicamentPrice.requestFocus();
            addMedicineBinding.progressCircle.setVisibility(View.GONE);
            return;
        }
        String Quantity = Objects.requireNonNull(addMedicineBinding.medicamentQuantity.getText()).toString().trim();
        if (Quantity.isEmpty()) {
            addMedicineBinding.medicamentQuantity.setError(getString(R.string.QuantityIsRequired));
            addMedicineBinding.medicamentQuantity.setFocusable(true);
            addMedicineBinding.medicamentQuantity.requestFocus();
            addMedicineBinding.progressCircle.setVisibility(View.GONE);
            return;
        }

        String Type = "";
        RadioButton selectedType;
        if (addMedicineBinding.radioGroupType.getCheckedRadioButtonId() == -1) {
            Toasty.info(Add_Medicine_Activity.this, getString(R.string.choose_type_Medicine), Toast.LENGTH_SHORT).show();
            addMedicineBinding.progressCircle.setVisibility(View.GONE);
            return;
        } else {
            selectedType = findViewById(addMedicineBinding.radioGroupType.getCheckedRadioButtonId());
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

        String QR = Objects.requireNonNull(addMedicineBinding.medicamentQR.getText()).toString().trim();
        if (QR.isEmpty()) {
            addMedicineBinding.medicamentQR.setError(getString(R.string.QRIsRequired));
            addMedicineBinding.medicamentQR.setFocusable(true);
            addMedicineBinding.medicamentQR.requestFocus();
            addMedicineBinding.progressCircle.setVisibility(View.GONE);
            return;
        }

        boolean isDiscount = addMedicineBinding.btnisDiscount.isChecked();
        String Discount = Objects.requireNonNull(addMedicineBinding.medicamentDiscount.getText()).toString().trim();
        if (isDiscount) {
            if (Discount.isEmpty()) {
                addMedicineBinding.medicamentDiscount.setError(getString(R.string.DiscountIsRequired));
                addMedicineBinding.medicamentDiscount.setFocusable(true);
                addMedicineBinding.medicamentDiscount.requestFocus();
                addMedicineBinding.progressCircle.setVisibility(View.GONE);
                return;
            }
        } else {
            Discount = "0.0";
        }

        String MedicineID = referenceMedicines.push().getKey();
        ModelMedicine medicine = new ModelMedicine();
        medicine.setMedicineID(MedicineID);
        medicine.setMedicineName(Name);
        medicine.setMedicinePrice(Double.parseDouble(Price));
        medicine.setNumOfMedicine(Integer.parseInt(Quantity));
        medicine.setMedicineType(Type);
        medicine.setMedicineQR(QR);
        medicine.setDiscount(isDiscount);
        medicine.setMedicineDiscount(Double.parseDouble(Discount));

        if (filePath == null) {
            medicine.setMedicineImage("https://firebasestorage.googleapis.com/v0/b/project-pharmacy-a28f1.appspot.com/o/image_medicines.jpg?alt=media&token=86f57e42-2079-43b9-a2cd-a5a63bfbfc46");
            assert MedicineID != null;
            referenceMedicines.child(MedicineID).setValue(medicine).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    addMedicineBinding.progressCircle.setVisibility(View.GONE);
                    Toasty.success(this, getResources().getString(R.string.Added_successfully), Toast.LENGTH_SHORT).show();
                    reset();
                }
            });
        } else {
            uploadImageSelect(filePath, medicine);
        }

    }

    public void reset() {
        addMedicineBinding.radioGroupType.check(-1);
        addMedicineBinding.medicamentImage.setImageResource(R.mipmap.add_image_medicine);
        addMedicineBinding.medicamentName.setText("");
        addMedicineBinding.medicamentPrice.setText("");
        addMedicineBinding.medicamentQuantity.setText("");
        addMedicineBinding.medicamentQR.setText("");
        addMedicineBinding.medicamentDiscount.setText("");
        addMedicineBinding.btnisDiscount.setChecked(false);
    }

    private void SelectImage() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(Add_Medicine_Activity.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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
                            .into(addMedicineBinding.medicamentImage);
                    addMedicineBinding.medicamentImage.setScaleType(ImageView.ScaleType.FIT_XY);
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
        addMedicineBinding.progressCircle.setVisibility(View.VISIBLE);
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
                                        addMedicineBinding.progressCircle.setVisibility(View.GONE);
                                        Toasty.success(this, getResources().getString(R.string.Added_successfully), Toast.LENGTH_SHORT).show();
                                        reset();
                                    }
                                });
                    });
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (!QRCode.isEmpty())
            addMedicineBinding.medicamentQR.setText(QRCode);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}