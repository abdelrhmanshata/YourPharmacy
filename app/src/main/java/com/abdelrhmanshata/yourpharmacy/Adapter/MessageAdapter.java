package com.abdelrhmanshata.yourpharmacy.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.abdelrhmanshata.yourpharmacy.Model.ModelChat;
import com.abdelrhmanshata.yourpharmacy.R;

import java.util.List;


public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;

    private Context mContext;
    private List<ModelChat> mChat;

    public MessageAdapter(Context mContext, List<ModelChat> mChat) {
        this.mChat = mChat;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.row_chat_right, parent, false);
            return new ViewHolder(view);
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.row_chat_left, parent, false);
            return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position) {

        ModelChat chat = mChat.get(position);

        holder.show_message.setText(chat.getMessage());
        holder.timeTV.setText(chat.getTime());
        holder.dateTV.setText(chat.getDate());
    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (mChat.get(position).getSender().equals("USER")) {
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView show_message;
        public TextView dateTV, timeTV;

        public ViewHolder(View itemView) {
            super(itemView);

            show_message = itemView.findViewById(R.id.show_message);
            dateTV = itemView.findViewById(R.id.dateTv);
            timeTV = itemView.findViewById(R.id.timeTv);
        }
    }
}