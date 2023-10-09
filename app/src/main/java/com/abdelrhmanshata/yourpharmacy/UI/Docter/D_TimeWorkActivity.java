package com.abdelrhmanshata.yourpharmacy.UI.Docter;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
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
import com.abdelrhmanshata.yourpharmacy.databinding.ActivityDTimeWorkBinding;

import java.util.Calendar;
import java.util.Objects;

public class D_TimeWorkActivity extends AppCompatActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference = database.getReference("TimeWork");

    ActivityDTimeWorkBinding timeWorkBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        timeWorkBinding = ActivityDTimeWorkBinding.inflate(getLayoutInflater());
        setContentView(timeWorkBinding.getRoot());

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Time Work Page");

        loadingTimeWork();

        timeWorkBinding.imageStart.setOnClickListener(v -> {
            Calendar mcurrentTime = Calendar.getInstance();
            int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
            int minute = mcurrentTime.get(Calendar.MINUTE);
            TimePickerDialog mTimePicker;

            mTimePicker = new TimePickerDialog(D_TimeWorkActivity.this,
                    (timePicker, selectedHour, selectedMinute) ->
                    {
                        timeWorkBinding.tvTimeStart.setText(selectedHour + ":" + selectedMinute);
                        reference.child("Time").child("TimeStart").setValue(selectedHour + ":" + selectedMinute);
                    }, hour, minute, true);
            mTimePicker.setTitle("Select Time");
            mTimePicker.show();
        });

        timeWorkBinding.imageEnd.setOnClickListener(v -> {
            Calendar mcurrentTime = Calendar.getInstance();
            int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
            int minute = mcurrentTime.get(Calendar.MINUTE);
            TimePickerDialog mTimePicker;
            mTimePicker = new TimePickerDialog(D_TimeWorkActivity.this,
                    (timePicker, selectedHour, selectedMinute) ->
                    {
                        timeWorkBinding.tvTimeEnd.setText(selectedHour + ":" + selectedMinute);
                        reference.child("Time").child("TimeEnd").setValue(selectedHour + ":" + selectedMinute);
                    }, hour, minute, true);
            mTimePicker.setTitle("Select Time");
            mTimePicker.show();
        });

        timeWorkBinding.Saturday.setOnCheckedChangeListener((buttonView, isChecked) -> reference.child("Day").child("Saturday").setValue(isChecked));
        timeWorkBinding.Sunday.setOnCheckedChangeListener((buttonView, isChecked) -> reference.child("Day").child("Sunday").setValue(isChecked));
        timeWorkBinding.Monday.setOnCheckedChangeListener((buttonView, isChecked) -> reference.child("Day").child("Monday").setValue(isChecked));
        timeWorkBinding.Tuesday.setOnCheckedChangeListener((buttonView, isChecked) -> reference.child("Day").child("Tuesday").setValue(isChecked));
        timeWorkBinding.Wednesday.setOnCheckedChangeListener((buttonView, isChecked) -> reference.child("Day").child("Wednesday").setValue(isChecked));
        timeWorkBinding.Thursday.setOnCheckedChangeListener((buttonView, isChecked) -> reference.child("Day").child("Thursday").setValue(isChecked));
        timeWorkBinding.Friday.setOnCheckedChangeListener((buttonView, isChecked) -> reference.child("Day").child("Friday").setValue(isChecked));

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