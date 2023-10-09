package com.abdelrhmanshata.yourpharmacy.Adapter;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.abdelrhmanshata.yourpharmacy.Model.ModelQuestion;
import com.abdelrhmanshata.yourpharmacy.R;

import java.util.List;

public class Adapter_Questions extends RecyclerView.Adapter<Adapter_Questions.ItemViewHolder> {

    private final Context mContext;
    private final List<ModelQuestion> questions;
    private OnItemClickListener mListener;

    public Adapter_Questions(Context mContext, List<ModelQuestion> questions) {
        this.mContext = mContext;
        this.questions = questions;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_item_question, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {

        ModelQuestion question = questions.get(position);
        holder.indexTV.setText(String.valueOf(position+1));
        holder.questionTV.setText(question.getQuestion());

        GradientDrawable colorCircle = (GradientDrawable) holder.indexTV.getBackground();
        int CircleColor = getIdColor(position+1);
        colorCircle.setColor(CircleColor);

    }

    @Override
    public int getItemCount() {
        return questions.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public interface OnItemClickListener {
        void onItem_Click(int position);
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        public TextView indexTV, questionTV;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            indexTV = itemView.findViewById(R.id.indexTV);
            questionTV = itemView.findViewById(R.id.questionTV);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    mListener.onItem_Click(position);
                }
            }
        }
    }

    private int getIdColor(int index) {
        int ColorResourceId;
        switch (index %10 ) {
            case 1:
                ColorResourceId = R.color.color1;
                break;
            case 2:
                ColorResourceId = R.color.color2;
                break;
            case 3:
                ColorResourceId = R.color.color3;
                break;
            case 4:
                ColorResourceId = R.color.color4;
                break;
            case 5:
                ColorResourceId = R.color.color5;
                break;
            case 6:
                ColorResourceId = R.color.color6;
                break;
            case 7:
                ColorResourceId = R.color.color7;
                break;
            case 8:
                ColorResourceId = R.color.color8;
                break;
            case 9:
                ColorResourceId = R.color.color9;
                break;
            default:
                ColorResourceId = R.color.color10;
                break;
        }
        return ContextCompat.getColor(mContext, ColorResourceId);
    }

}
