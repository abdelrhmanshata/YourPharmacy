package com.abdelrhmanshata.yourpharmacy.UI.User;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.abdelrhmanshata.yourpharmacy.Adapter.Adapter_Questions;
import com.abdelrhmanshata.yourpharmacy.Adapter.MessageAdapter;
import com.abdelrhmanshata.yourpharmacy.Model.ModelChat;
import com.abdelrhmanshata.yourpharmacy.Model.ModelQuestion;
import com.abdelrhmanshata.yourpharmacy.R;
import com.abdelrhmanshata.yourpharmacy.databinding.ActivityUChatBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import es.dmoral.toasty.Toasty;

public class U_Chat_Activity extends AppCompatActivity implements Adapter_Questions.OnItemClickListener {

    ActivityUChatBinding chatBinding;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference = database.getReference("Communication");
    List<ModelQuestion> questions;
    Adapter_Questions adapterQuestions;

    String Phone_Number = "0123456789";

    MessageAdapter messageAdapter;
    List<ModelChat> mchat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        chatBinding = ActivityUChatBinding.inflate(getLayoutInflater());
        setContentView(chatBinding.getRoot());

        setSupportActionBar(chatBinding.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Chat Page");

        Initialize_variables();

        chatBinding.addQuestion.setOnClickListener(v -> {
            if (!questions.isEmpty()) {
                chatBinding.layoutQuestions.setVisibility(View.VISIBLE);
            } else {
                Toasty.warning(this, "No Question Found !!!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void Initialize_variables() {
        chatBinding.QuestionsRecyclerview.setHasFixedSize(true);
        chatBinding.QuestionsRecyclerview.setLayoutManager(new LinearLayoutManager(this));
        questions = new ArrayList<>();
        adapterQuestions = new Adapter_Questions(this, questions);
        chatBinding.QuestionsRecyclerview.setAdapter(adapterQuestions);
        adapterQuestions.setOnItemClickListener(this);

        chatBinding.ChatRecyclerview.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        chatBinding.ChatRecyclerview.setLayoutManager(linearLayoutManager);
        mchat = new ArrayList<>();
        messageAdapter = new MessageAdapter(this, mchat);
        chatBinding.ChatRecyclerview.setAdapter(messageAdapter);

        loadingAllQuestions();
    }

    public void loadingAllQuestions() {
        reference
                .child("Phone").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Phone_Number = snapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("U_Chat_Activity", error.getMessage());
            }
        });

        reference
                .child("Questions")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        questions.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            ModelQuestion question = snapshot.getValue(ModelQuestion.class);
                            if (question != null)
                                questions.add(question);
                        }
                        adapterQuestions.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d("U_Chat_Activity", error.getMessage());
                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void onItem_Click(int position) {
        ModelQuestion question = questions.get(position);
        mchat.add(new ModelChat(question.getQuestionID(), "USER", "BOT", question.getQuestion(), getCurrentDate(), getCurrentTime()));
        mchat.add(new ModelChat(question.getQuestionID(), "BOT", "USER", question.getAnswer(), getCurrentDate(), getCurrentTime()));
        messageAdapter.notifyDataSetChanged();
    }

    public String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("yyyy/M/dd", Locale.ENGLISH);
        return mdformat.format(calendar.getTime());
    }

    public String getCurrentTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
        return mdformat.format(calendar.getTime());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chat_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.home:
                onBackPressed();
                break;
            case R.id.phone:
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + Uri.encode(Phone_Number)));
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}