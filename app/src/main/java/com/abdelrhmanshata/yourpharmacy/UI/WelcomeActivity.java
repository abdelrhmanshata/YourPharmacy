package com.abdelrhmanshata.yourpharmacy.UI;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.abdelrhmanshata.yourpharmacy.UI.Login_Register.LoginActivity;
import com.abdelrhmanshata.yourpharmacy.Model.ModelDataUser;
import com.abdelrhmanshata.yourpharmacy.R;
import com.abdelrhmanshata.yourpharmacy.UI.Docter.D_Home_Activity;
import com.abdelrhmanshata.yourpharmacy.UI.User.U_Home_Activity;

import es.dmoral.toasty.Toasty;

public class WelcomeActivity extends AppCompatActivity {

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = mAuth.getCurrentUser();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = database.getReference("AllUsers");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);


        Thread welcome = new Thread(() -> {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (firebaseUser != null && firebaseUser.getUid() != null) {
                databaseReference
                        .child(firebaseUser.getUid())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                ModelDataUser user = snapshot.getValue(ModelDataUser.class);
                                if (user != null) {
                                    if (user.getUserType().toLowerCase().contains("docter")) {
                                        if (user.isUserAdmin()) {
                                            startActivity(new Intent(WelcomeActivity.this, D_Home_Activity.class));
                                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                            finish();
                                        } else {
                                            Toasty.info(WelcomeActivity.this, getString(R.string.userNotPermission), Toast.LENGTH_SHORT).show();
                                            mAuth.signOut();
                                        }
                                    } else if (user.getUserType().toLowerCase().contains("user")) {
                                        startActivity(new Intent(WelcomeActivity.this, U_Home_Activity.class));
                                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                        finish();
                                    }
                                } else {
                                    Toast.makeText(WelcomeActivity.this, getString(R.string.userNotFound), Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.d("WelcomeActivity", error.getMessage());
                            }
                        });
            } else {
                startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
            }
        });
        welcome.start();

    }
}