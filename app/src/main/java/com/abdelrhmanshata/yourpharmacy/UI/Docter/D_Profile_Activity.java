package com.abdelrhmanshata.yourpharmacy.UI.Docter;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.abdelrhmanshata.yourpharmacy.Model.ModelDataUser;
import com.abdelrhmanshata.yourpharmacy.R;
import com.abdelrhmanshata.yourpharmacy.databinding.ActivityDocterProfileBinding;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.Objects;

import es.dmoral.toasty.Toasty;

public class D_Profile_Activity extends AppCompatActivity {

    ActivityDocterProfileBinding profileBinding;

    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference reference = firebaseDatabase.getReference("AllUsers");

    Uri filePath;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageReference = storage.getReference("DoctorsImage");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        profileBinding = ActivityDocterProfileBinding.inflate(getLayoutInflater());
        setContentView(profileBinding.getRoot());

        // Permissions to open Camera
        ActivityCompat.requestPermissions(D_Profile_Activity.this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                1);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        loadingDocterData();

        profileBinding.updatabtn.setOnClickListener(v -> {

            profileBinding.progressCircle.setVisibility(View.VISIBLE);

            String fullName = Objects.requireNonNull(profileBinding.userName.getText()).toString().trim();
            if (fullName.isEmpty()) {
                profileBinding.userName.setError(getString(R.string.userNameIsRequired));
                profileBinding.userName.setFocusable(true);
                profileBinding.userName.requestFocus();
                profileBinding.progressCircle.setVisibility(View.GONE);
                return;
            }

            String UserIDNumber = Objects.requireNonNull(profileBinding.userIDNumber.getText()).toString().trim();
            if (UserIDNumber.isEmpty()) {
                profileBinding.userIDNumber.setError(getString(R.string.IdentificationNumberIsRequired));
                profileBinding.userIDNumber.setFocusable(true);
                profileBinding.userIDNumber.requestFocus();
                profileBinding.progressCircle.setVisibility(View.GONE);
                return;
            }

            String UserAge = Objects.requireNonNull(profileBinding.userAge.getText()).toString().trim();
            if (UserAge.isEmpty()) {
                profileBinding.userAge.setError(getString(R.string.userAgeIsRequired));
                profileBinding.userAge.setFocusable(true);
                profileBinding.userAge.requestFocus();
                profileBinding.progressCircle.setVisibility(View.GONE);
                return;
            }

            String UserPhoneNumber = Objects.requireNonNull(profileBinding.userPhoneNumber.getText()).toString().trim();
            if (TextUtils.isEmpty(UserPhoneNumber)) {
                profileBinding.userPhoneNumber.setError(getString(R.string.phoneNumberIsRequired));
                profileBinding.userPhoneNumber.setFocusable(true);
                profileBinding.userPhoneNumber.requestFocus();
                profileBinding.progressCircle.setVisibility(View.GONE);
                return;
            }

            String UserAddress = Objects.requireNonNull(profileBinding.userAddress.getText()).toString().trim();
            if (UserAddress.isEmpty()) {
                profileBinding.userAddress.setError(getString(R.string.userAddressIsRequired));
                profileBinding.userAddress.setFocusable(true);
                profileBinding.userAddress.requestFocus();
                profileBinding.progressCircle.setVisibility(View.GONE);
                return;
            }

            UpdataData(fullName, UserIDNumber, UserAge, UserPhoneNumber, UserAddress);
        });

        profileBinding.userImage.setOnClickListener(v -> SelectImage());
    }

    private void UpdataData(String... data) {

        reference
                .child(user.getUid())
                .child("userName")
                .setValue(data[0]);

        reference
                .child(user.getUid())
                .child("userIDNumber")
                .setValue(data[1]);

        reference
                .child(user.getUid())
                .child("userAge")
                .setValue(data[2]);


        reference
                .child(user.getUid())
                .child("userPhone")
                .setValue(data[3]);

        reference
                .child(user.getUid())
                .child("userAddress")
                .setValue(data[4]);

        Toasty.success(D_Profile_Activity.this, "" + getResources().getString(R.string.updatedSuccessfully), Toast.LENGTH_SHORT).show();
    }

    private void SelectImage() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(D_Profile_Activity.this);
    }

    private void loadingDocterData() {
        profileBinding.circleProgressBar.setVisibility(View.VISIBLE);
        reference
                .child(user.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ModelDataUser modelDataUser = snapshot.getValue(ModelDataUser.class);
                        if (modelDataUser != null) {

                            Picasso.get().load(modelDataUser.getUserImageUri().trim())
                                    .into(profileBinding.userImage);

                            profileBinding.userEmail.setText(modelDataUser.getUserEmail());
                            profileBinding.userName.setText(modelDataUser.getUserName());
                            profileBinding.userIDNumber.setText(modelDataUser.getUserIDNumber());
                            profileBinding.userAge.setText(modelDataUser.getUserAge());
                            profileBinding.userPhoneNumber.setText(modelDataUser.getUserPhone());
                            profileBinding.userAddress.setText(modelDataUser.getUserAddress());
                        }
                        profileBinding.circleProgressBar.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d("D_Profile_Activity", error.getMessage());
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                Toasty.normal(D_Profile_Activity.this, getResources().getString(R.string.permissionDenied), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                filePath = result.getUri();
                uploadImage(filePath);
                try {
                    Picasso.get().load(filePath).into(profileBinding.userImage);
                } catch (Exception e) {
                    Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Log.d("Error", error.getMessage());
            }
        }
    }

    private void uploadImage(Uri filePath) {
        if (filePath != null) {
            profileBinding.waveLoadingView.setVisibility(View.VISIBLE);
            StorageReference ref = storageReference
                    .child(user.getUid() + "/DocterImage.jpeg");
            ref.putFile(filePath)
                    .addOnSuccessListener(taskSnapshot -> {
                        ref.getDownloadUrl()
                                .addOnSuccessListener(uri -> {
                                    if (!reference.onDisconnect().cancel().isSuccessful()) {
                                        profileBinding.waveLoadingView.setVisibility(View.GONE);
                                        Toasty.success(D_Profile_Activity.this, getString(R.string.updatedSuccessfully), Toast.LENGTH_SHORT).show();
                                        reference
                                                .child(user.getUid())
                                                .child("userImageUri")
                                                .setValue(uri.toString());
                                    } else {
                                        Toasty.error(D_Profile_Activity.this, getString(R.string.Updatefailed), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }).addOnFailureListener(e -> {
                profileBinding.waveLoadingView.setVisibility(View.GONE);
                Log.d("Failure Error", e.getMessage());
            }).addOnProgressListener(taskSnapshot -> {
                double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                profileBinding.waveLoadingView.setCenterTitle((int) progress + " %");
                profileBinding.waveLoadingView.setProgressValue((int) progress);
            });
        }
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