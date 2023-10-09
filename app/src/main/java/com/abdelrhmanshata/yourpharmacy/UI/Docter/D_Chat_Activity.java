package com.abdelrhmanshata.yourpharmacy.UI.Docter;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.abdelrhmanshata.yourpharmacy.Adapter.Adapter_Questions;
import com.abdelrhmanshata.yourpharmacy.Model.ModelQuestion;
import com.abdelrhmanshata.yourpharmacy.R;
import com.abdelrhmanshata.yourpharmacy.databinding.ActivityDChatBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import es.dmoral.toasty.Toasty;

public class D_Chat_Activity extends AppCompatActivity implements Adapter_Questions.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener {

    ActivityDChatBinding chatBinding;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference = database.getReference("Communication");
    List<ModelQuestion> questions;
    Adapter_Questions adapterQuestions;
    SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        chatBinding = ActivityDChatBinding.inflate(getLayoutInflater());
        setContentView(chatBinding.getRoot());

        setSupportActionBar(chatBinding.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Questions Page");

        Initialize_variables();

        chatBinding.addNewQuestion.setOnClickListener(v -> showDialogNewQuestion(null));

    }

    public void Initialize_variables() {
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        chatBinding.QuestionsRecyclerview.setHasFixedSize(true);
        chatBinding.QuestionsRecyclerview.setLayoutManager(new LinearLayoutManager(this));

        questions = new ArrayList<>();

        adapterQuestions = new Adapter_Questions(this, questions);
        chatBinding.QuestionsRecyclerview.setAdapter(adapterQuestions);
        adapterQuestions.setOnItemClickListener(this);

        loadingAllQuestions();

    }

    public void loadingAllQuestions() {
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
                        if (questions.isEmpty())
                            chatBinding.QuestionsRecyclerviewImage.setVisibility(View.VISIBLE);
                        else
                            chatBinding.QuestionsRecyclerviewImage.setVisibility(View.GONE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d("D_Chat_Activity", error.getMessage());
                    }
                });
    }

    @SuppressLint("CheckResult")
    public void showDialogNewQuestion(ModelQuestion modelQuestion) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.add_questions_layout, null);
        dialogBuilder.setView(dialogView);
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
        //
        TextInputEditText Question = dialogView.findViewById(R.id.Question);
        TextInputEditText AnswerQuestion = dialogView.findViewById(R.id.AnswerQuestion);
        RelativeLayout layout = dialogView.findViewById(R.id.addQuestion);
        ProgressBar progressBar = dialogView.findViewById(R.id.progressSave);

        if (modelQuestion != null) {
            Question.setText(modelQuestion.getQuestion());
            AnswerQuestion.setText(modelQuestion.getAnswer());
        }

        layout.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            String sQuestion = Objects.requireNonNull(Question.getText()).toString().trim();
            if (sQuestion.isEmpty()) {
                Question.setError(getString(R.string.PleaseEnterQuestion));
                Question.setFocusable(true);
                Question.requestFocus();
                progressBar.setVisibility(View.GONE);
                return;
            }
            String sAnswer = Objects.requireNonNull(AnswerQuestion.getText()).toString().trim();
            if (sAnswer.isEmpty()) {
                AnswerQuestion.setError(getString(R.string.PleaseEnterAnswer));
                AnswerQuestion.setFocusable(true);
                AnswerQuestion.requestFocus();
                progressBar.setVisibility(View.GONE);
                return;
            }
            ModelQuestion question = new ModelQuestion();
            if (modelQuestion == null) {
                question.setQuestionID(reference.push().getKey());
            } else {
                question.setQuestionID(modelQuestion.getQuestionID());
            }
            question.setQuestion(sQuestion);
            question.setAnswer(sAnswer);
            reference
                    .child("Questions")
                    .child(question.getQuestionID())
                    .setValue(question).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    progressBar.setVisibility(View.GONE);
                    Toasty.success(D_Chat_Activity.this, getResources().getString(R.string.Added_successfully), Toast.LENGTH_SHORT).show();;
                }
            });
            alertDialog.dismiss();
        });
    }

    @SuppressLint("CheckResult")
    public void showDialogAddPhone() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.add_phone_layout, null);
        dialogBuilder.setView(dialogView);
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
        //
        TextInputEditText Phone = dialogView.findViewById(R.id.Phone);
        RelativeLayout layout = dialogView.findViewById(R.id.addQuestion);
        ProgressBar progressBar = dialogView.findViewById(R.id.progressSave);

        layout.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            String sPhone = Objects.requireNonNull(Phone.getText()).toString().trim();
            if (sPhone.isEmpty()) {
                Phone.setError(getString(R.string.phoneNumberIsRequired));
                Phone.setFocusable(true);
                Phone.requestFocus();
                progressBar.setVisibility(View.GONE);
                return;
            }
            reference
                    .child("Phone")
                    .setValue(sPhone)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            progressBar.setVisibility(View.GONE);
                            Toasty.success(D_Chat_Activity.this, getResources().getString(R.string.Added_successfully), Toast.LENGTH_SHORT).show();
                        }
                    });
            alertDialog.dismiss();
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
        showDialogNewQuestion(question);
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(() -> {
            mSwipeRefreshLayout.setRefreshing(false);
            loadingAllQuestions();
        }, 1000);
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
                showDialogAddPhone();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}

