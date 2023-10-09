package com.abdelrhmanshata.yourpharmacy.UI.User;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.abdelrhmanshata.yourpharmacy.R;
import com.abdelrhmanshata.yourpharmacy.databinding.ActivityUTimeWorkBinding;

import java.util.Objects;

public class U_TimeWorkActivity extends AppCompatActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference = database.getReference("TimeWork");

    ActivityUTimeWorkBinding timeWorkBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        timeWorkBinding = ActivityUTimeWorkBinding.inflate(getLayoutInflater());
        setContentView(timeWorkBinding.getRoot());

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Time Work Page");

        loadingTimeWork();

    }

    public void loadingTimeWork() {
        reference
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String TimeStart = snapshot.child("Time").child("TimeStart").getValue(String.class);
                        String TimeEnd = snapshot.child("Time").child("TimeEnd").getValue(String.class);

                        boolean Saturday = snapshot.child("Day").child("Saturday").getValue(boolean.class);
                        boolean Sunday = snapshot.child("Day").child("Sunday").getValue(boolean.class);
                        boolean Monday = snapshot.child("Day").child("Monday").getValue(boolean.class);
                        boolean Tuesday = snapshot.child("Day").child("Tuesday").getValue(boolean.class);
                        boolean Wednesday = snapshot.child("Day").child("Wednesday").getValue(boolean.class);
                        boolean Thursday = snapshot.child("Day").child("Thursday").getValue(boolean.class);
                        boolean Friday = snapshot.child("Day").child("Friday").getValue(boolean.class);

                        timeWorkBinding.tvTimeStart.setText(TimeStart);
                        timeWorkBinding.tvTimeEnd.setText(TimeEnd);

                        timeWorkBinding.Saturday.setChecked(Saturday);
                        timeWorkBinding.Sunday.setChecked(Sunday);
                        timeWorkBinding.Monday.setChecked(Monday);
                        timeWorkBinding.Tuesday.setChecked(Tuesday);
                        timeWorkBinding.Wednesday.setChecked(Wednesday);
                        timeWorkBinding.Thursday.setChecked(Thursday);
                        timeWorkBinding.Friday.setChecked(Friday);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


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