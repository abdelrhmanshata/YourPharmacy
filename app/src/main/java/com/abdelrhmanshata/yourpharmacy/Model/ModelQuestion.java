package com.abdelrhmanshata.yourpharmacy.Model;

import java.io.Serializable;

public class ModelQuestion implements Serializable {
    String questionID,
            Question,
            Answer;

    public ModelQuestion() {
    }

    public ModelQuestion(String questionID, String question, String answer) {
        this.questionID = questionID;
        Question = question;
        Answer = answer;
    }

    public String getQuestionID() {
        return questionID;
    }

    public void setQuestionID(String questionID) {
        this.questionID = questionID;
    }

    public String getQuestion() {
        return Question;
    }

    public void setQuestion(String question) {
        Question = question;
    }

    public String getAnswer() {
        return Answer;
    }

    public void setAnswer(String answer) {
        Answer = answer;
    }
}
