package com.abdelrhmanshata.yourpharmacy.UI.Login_Register;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.abdelrhmanshata.yourpharmacy.Model.ModelDataUser;
import com.abdelrhmanshata.yourpharmacy.R;
import com.abdelrhmanshata.yourpharmacy.UI.Docter.D_Home_Activity;
import com.abdelrhmanshata.yourpharmacy.UI.User.U_Home_Activity;
import com.abdelrhmanshata.yourpharmacy.databinding.ActivityLoginBinding;

import java.util.Objects;

import es.dmoral.toasty.Toasty;

public class LoginActivity extends AppCompatActivity {

    ActivityLoginBinding loginBinding;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference = database.getReference("AllUsers");
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private AlertDialog alertDialog_RecoverPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loginBinding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(loginBinding.getRoot());

        loginBinding.userLogin.setOnClickListener(v -> loginAccount());

        loginBinding.forgetPassword.setOnClickListener(v -> {
            showDialog_RecoverPassword();
        });

        loginBinding.userRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
    }

    private void loginAccount() {

        String UserEmail = Objects.requireNonNull(loginBinding.userEmail.getText()).toString().trim();
        String UserPassword = Objects.requireNonNull(loginBinding.userPassword.getText()).toString().trim();

        loginBinding.progressCircle.setVisibility(View.VISIBLE);

        if (UserEmail.isEmpty()) {
            loginBinding.userEmail.setError(getString(R.string.emailIsRequired));
            loginBinding.userEmail.requestFocus();
            loginBinding.progressCircle.setVisibility(View.GONE);
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(UserEmail).matches()) {
            loginBinding.userEmail.setError(getString(R.string.please_enter_valid_email));
            loginBinding.userEmail.requestFocus();
            loginBinding.progressCircle.setVisibility(View.GONE);
            return;
        }

        if (UserPassword.isEmpty()) {
            loginBinding.userPassword.setError(getString(R.string.passwordIsRequired));
            loginBinding.userPassword.requestFocus();
            loginBinding.progressCircle.setVisibility(View.GONE);
            return;
        }

        if (UserPassword.length() < 8) {
            loginBinding.userPassword.setError(getString(R.string.minimumLength));
            loginBinding.userPassword.requestFocus();
            loginBinding.progressCircle.setVisibility(View.GONE);
            return;
        }

        mAuth.signInWithEmailAndPassword(UserEmail, UserPassword)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        reference
                                .child(mAuth.getCurrentUser().getUid())
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        ModelDataUser user = snapshot.getValue(ModelDataUser.class);
                                        if (user != null) {
                                            loginBinding.progressCircle.setVisibility(View.GONE);
                                            if (user.getUserType().toLowerCase().contains("docter")) {
                                                if(user.isUserAdmin()){
                                                    startActivity(new Intent(LoginActivity.this, D_Home_Activity.class));
                                                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                                    finish();
                                                }else{
                                                    Toasty.info(LoginActivity.this, getString(R.string.userNotPermission), Toast.LENGTH_SHORT).show();
                                                    mAuth.signOut();
                                                }
                                            } else if (user.getUserType().toLowerCase().contains("user")) {
                                                startActivity(new Intent(LoginActivity.this, U_Home_Activity.class));
                                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                                finish();
                                            }
                                            loginBinding.progressCircle.setVisibility(View.GONE);

                                        } else {
                                            loginBinding.progressCircle.setVisibility(View.GONE);
                                            Toasty.warning(LoginActivity.this, getString(R.string.userNotFound), Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        loginBinding.progressCircle.setVisibility(View.GONE);
                                        Log.d("LoginActivity", error.getMessage());
                                    }
                                });
                    } else if (task.getException() instanceof FirebaseNetworkException) {
                        Toasty.error(LoginActivity.this, "No Connectionn ... ", Toast.LENGTH_SHORT).show();
                    } else if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                        Toasty.info(LoginActivity.this, "This User Not Found ... ", Toast.LENGTH_SHORT).show();
                    } else if ((task.getException() instanceof FirebaseAuthInvalidCredentialsException)) {
                        Toasty.warning(LoginActivity.this, "This Password Not Right ... ", Toast.LENGTH_SHORT).show();
                    } else if (task.getException() instanceof FirebaseTooManyRequestsException) {
                        Toasty.custom(LoginActivity.this, "You Try Enter Password Many Times \n Try After One Minute", getResources().getDrawable(R.drawable.ic_timer_24), Toast.LENGTH_SHORT, true).show();
                    } else {
                        Toasty.error(LoginActivity.this, "Error+->" + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                    loginBinding.progressCircle.setVisibility(View.GONE);
                });
    }

    public void showDialog_RecoverPassword() {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        View layout = getLayoutInflater().inflate(R.layout.activity_recover_password, null);
        TextInputEditText userEmail = layout.findViewById(R.id.userEmail);
        Button btRecover = layout.findViewById(R.id.btRecover);
        Button btCancel = layout.findViewById(R.id.btCancel);
        ProgressBar progressBar = layout.findViewById(R.id.progressCircle);
        btRecover.setOnClickListener(view -> RecoverPassword(userEmail, progressBar));
        btCancel.setOnClickListener(v -> {
            alertDialog_RecoverPassword.dismiss();
            alertDialog_RecoverPassword.cancel();
        });
        alertBuilder.setView(layout);
        alertDialog_RecoverPassword = alertBuilder.create();
        alertDialog_RecoverPassword.show();
    }

    public void RecoverPassword(TextInputEditText userEmail, ProgressBar progressBar) {

        String EmailAddress = Objects.requireNonNull(userEmail.getText()).toString().trim();
        progressBar.setVisibility(View.VISIBLE);

        if (EmailAddress.isEmpty()) {
            userEmail.setError(getString(R.string.emailIsRequired));
            userEmail.setFocusable(true);
            userEmail.requestFocus();
            progressBar.setVisibility(View.GONE);
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(EmailAddress).matches()) {
            userEmail.setError(getString(R.string.please_enter_valid_email));
            userEmail.setFocusable(true);
            userEmail.requestFocus();
            progressBar.setVisibility(View.GONE);
            return;
        }

        mAuth.sendPasswordResetEmail(EmailAddress).addOnCompleteListener(task -> {
            progressBar.setVisibility(View.GONE);
            if (task.isSuccessful()) {
                Toasty.info(LoginActivity.this, getString(R.string.checkMesg) + EmailAddress, Toast.LENGTH_SHORT).show();
                alertDialog_RecoverPassword.dismiss();
                alertDialog_RecoverPassword.cancel();
            } else {
                Toasty.error(LoginActivity.this, "Falid ..." + Objects.requireNonNull(task.getException()).toString(), Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            progressBar.setVisibility(View.GONE);
            Toasty.error(LoginActivity.this, "Error ->" + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

}