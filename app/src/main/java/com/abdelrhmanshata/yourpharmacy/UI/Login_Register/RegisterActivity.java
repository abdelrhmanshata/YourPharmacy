package com.abdelrhmanshata.yourpharmacy.UI.Login_Register;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.abdelrhmanshata.yourpharmacy.Model.ModelDataUser;
import com.abdelrhmanshata.yourpharmacy.R;
import com.abdelrhmanshata.yourpharmacy.databinding.ActivityRegisterBinding;

import java.util.Objects;

import es.dmoral.toasty.Toasty;

public class RegisterActivity extends AppCompatActivity {

    ActivityRegisterBinding registerBinding;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerBinding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(registerBinding.getRoot());
        Initialize_variables();
        registerBinding.continueRegister.setOnClickListener(v -> {
            continueRegister();
        });
    }

    public void Initialize_variables() {

        firebaseAuth = FirebaseAuth.getInstance();

        setSupportActionBar(registerBinding.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.down_to_up);
        registerBinding.cv.setAnimation(animation);
    }

    public void continueRegister() {

        registerBinding.progressCircle.setVisibility(View.VISIBLE);

        String Username = Objects.requireNonNull(registerBinding.userName.getText()).toString().trim();

        if (Username.isEmpty()) {
            registerBinding.userName.setError(getString(R.string.userNameIsRequired));
            registerBinding.userName.setFocusable(true);
            registerBinding.userName.requestFocus();
            registerBinding.progressCircle.setVisibility(View.GONE);
            return;
        }

        String UserEmail = Objects.requireNonNull(registerBinding.userEmail.getText()).toString().trim();
        if (UserEmail.isEmpty()) {
            registerBinding.userEmail.setError(getString(R.string.emailIsRequired));
            registerBinding.userEmail.setFocusable(true);
            registerBinding.userEmail.requestFocus();
            registerBinding.progressCircle.setVisibility(View.GONE);
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(UserEmail).matches()) {
            registerBinding.userEmail.setError(getString(R.string.please_enter_valid_email));
            registerBinding.userEmail.setFocusable(true);
            registerBinding.userEmail.requestFocus();
            registerBinding.progressCircle.setVisibility(View.GONE);
            return;
        }

        String UserPassword = Objects.requireNonNull(registerBinding.userPassword.getText()).toString().trim();
        String ConfirmPassword = Objects.requireNonNull(registerBinding.confirmPassword.getText()).toString().trim();
        String checkPassword = "^" +
                "(?=.*[a-zA-Z])" +      //any letter
                "(?=S+$)" +           //no white spaces
                "$";

        if (UserPassword.isEmpty()) {
            registerBinding.userPassword.setError(getString(R.string.passwordIsRequired));
            registerBinding.userPassword.setFocusable(true);
            registerBinding.userPassword.requestFocus();
            registerBinding.progressCircle.setVisibility(View.GONE);
            return;
        }

        if (UserPassword.length() < 8) {
            registerBinding.userPassword.setError(getString(R.string.minimumLength));
            registerBinding.userPassword.setFocusable(true);
            registerBinding.userPassword.requestFocus();
            registerBinding.progressCircle.setVisibility(View.GONE);
            return;
        }

        if (UserPassword.matches(checkPassword)) {
            registerBinding.userPassword.setError(getString(R.string.passwordNotSpace));
            registerBinding.userPassword.setFocusable(true);
            registerBinding.userPassword.requestFocus();
            registerBinding.progressCircle.setVisibility(View.GONE);
            return;
        }

        if (!ConfirmPassword.equals(UserPassword)) {
            registerBinding.confirmPassword.setError(getString(R.string.passwordDontMatch));
            registerBinding.confirmPassword.setFocusable(true);
            registerBinding.confirmPassword.requestFocus();
            registerBinding.progressCircle.setVisibility(View.GONE);
            return;
        }

        String UserIDNumber = Objects.requireNonNull(registerBinding.userIDNumber.getText()).toString().trim();
        if (UserIDNumber.isEmpty()) {
            registerBinding.userIDNumber.setError(getString(R.string.IdentificationNumberIsRequired));
            registerBinding.userIDNumber.setFocusable(true);
            registerBinding.userIDNumber.requestFocus();
            registerBinding.progressCircle.setVisibility(View.GONE);
            return;
        }

        String UserAge = Objects.requireNonNull(registerBinding.userAge.getText()).toString().trim();
        if (UserAge.isEmpty()) {
            registerBinding.userAge.setError(getString(R.string.userAgeIsRequired));
            registerBinding.userAge.setFocusable(true);
            registerBinding.userAge.requestFocus();
            registerBinding.progressCircle.setVisibility(View.GONE);
            return;
        }

        if (Integer.parseInt(UserAge) < 18) {
            registerBinding.userAge.setError(getString(R.string.user_age_Limit));
            registerBinding.userAge.setFocusable(true);
            registerBinding.userAge.requestFocus();
            registerBinding.progressCircle.setVisibility(View.GONE);
            return;
        }

        //Get complete phone number
        String UserPhone = registerBinding.userPhoneNumber.getText().toString();
        if (TextUtils.isEmpty(UserPhone)) {
            registerBinding.userPhoneNumber.setError(getString(R.string.phone_number_is_required));
            registerBinding.userPhoneNumber.setFocusable(true);
            registerBinding.userPhoneNumber.requestFocus();
            registerBinding.progressCircle.setVisibility(View.GONE);
            return;
        }

        if (UserPhone.length() < 10) {
            registerBinding.userPhoneNumber.setError(getString(R.string.minimumLengthPhone));
            registerBinding.userPhoneNumber.setFocusable(true);
            registerBinding.userPhoneNumber.requestFocus();
            registerBinding.progressCircle.setVisibility(View.GONE);
            return;
        }

        String UserAddress = Objects.requireNonNull(registerBinding.userAddress.getText()).toString().trim();
        if (UserAddress.isEmpty()) {
            registerBinding.userAddress.setError(getString(R.string.userAddressIsRequired));
            registerBinding.userAddress.setFocusable(true);
            registerBinding.userAddress.requestFocus();
            registerBinding.progressCircle.setVisibility(View.GONE);
            return;
        }

        String Type = "";
        RadioButton selectedType;
        if (registerBinding.radioGroupType.getCheckedRadioButtonId() == -1) {
            Toasty.info(RegisterActivity.this, getString(R.string.please_select_type), Toast.LENGTH_SHORT).show();
            registerBinding.progressCircle.setVisibility(View.GONE);
            return;
        } else {
            selectedType = findViewById(registerBinding.radioGroupType.getCheckedRadioButtonId());
            if (selectedType.getId() == R.id.docter) {
                Type = "Docter";
            } else {
                Type = "User";
            }
        }

        ModelDataUser modelDataUser = new ModelDataUser();
        modelDataUser.setUserName(Username);
        modelDataUser.setUserEmail(UserEmail);
        modelDataUser.setUserPassword(UserPassword);
        modelDataUser.setUserIDNumber(UserIDNumber);
        modelDataUser.setUserAge(UserAge);
        modelDataUser.setUserPhone(UserPhone);
        modelDataUser.setUserAddress(UserAddress);
        modelDataUser.setUserType(Type);
        RegisterUser(modelDataUser);
    }

    public void RegisterUser(ModelDataUser modelDataUser) {
        registerBinding.progressCircle.setVisibility(View.VISIBLE);

        firebaseAuth
                .createUserWithEmailAndPassword(modelDataUser.getUserEmail(), modelDataUser.getUserPassword())
                .addOnCompleteListener(task -> {

                    registerBinding.progressCircle.setVisibility(View.GONE);
                    if (task.isSuccessful()) {

                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference referenceAllUser = database.getReference("AllUsers");

                        if (user != null) {
                            modelDataUser.setUserID(user.getUid());
                            if (modelDataUser.getUserType().toLowerCase().contains("docter")) {
                                modelDataUser.setUserImageUri("https://firebasestorage.googleapis.com/v0/b/project-pharmacy-a28f1.appspot.com/o/surgeon.png?alt=media&token=b0b0be02-d289-46d0-9d6f-a02f9e9ec788");
                                referenceAllUser.child(modelDataUser.getUserID()).setValue(modelDataUser);
                            } else if (modelDataUser.getUserType().toLowerCase().contains("user")) {
                                modelDataUser.setUserImageUri("https://firebasestorage.googleapis.com/v0/b/project-pharmacy-a28f1.appspot.com/o/profile.png?alt=media&token=10a1bb47-df32-4890-b1e1-95447d6b7385");
                                referenceAllUser.child(modelDataUser.getUserID()).setValue(modelDataUser);
                            }

                            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(modelDataUser.getUserName())
                                    .build();
                            user.updateProfile(profile).addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {

                                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                    finish();
                                }
                            });
                        }
                    } else {
                        if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                            Toasty.info(RegisterActivity.this, getString(R.string.AlreadyRegistered), Toast.LENGTH_SHORT).show();
                        } else if (task.getException() instanceof FirebaseNetworkException) {
                            Toasty.warning(RegisterActivity.this, "No Connectionn ... ", Toast.LENGTH_SHORT).show();
                        } else {
                            Toasty.error(RegisterActivity.this, "Exception -> " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(e -> {
            registerBinding.progressCircle.setVisibility(View.GONE);
            if (e instanceof FirebaseAuthUserCollisionException) {
                Toasty.info(RegisterActivity.this, getString(R.string.AlreadyRegistered), Toast.LENGTH_SHORT).show();
            } else if (e instanceof FirebaseNetworkException) {
                Toasty.warning(RegisterActivity.this, "No Connectionn ... ", Toast.LENGTH_SHORT).show();
            } else {
                Toasty.error(RegisterActivity.this, "Exception -> " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, LoginActivity.class));
        this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}