package com.abdelrhmanshata.yourpharmacy.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.abdelrhmanshata.yourpharmacy.Model.ModelDataUser;
import com.abdelrhmanshata.yourpharmacy.R;

import java.util.List;

public class Adapter_Users extends RecyclerView.Adapter<Adapter_Users.ItemViewHolder> {

    private final Context mContext;
    private final List<ModelDataUser> users;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = database.getReference("AllUsers");

    public Adapter_Users(Context mContext, List<ModelDataUser> users) {
        this.mContext = mContext;
        this.users = users;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_item_users, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {

        ModelDataUser user = users.get(position);

        holder.userIsAdmin.setChecked(user.isUserAdmin());
        holder.userName.setText(user.getUserName());
        holder.emailTV.setText(user.getUserEmail());

        holder.userIsAdmin.setOnCheckedChangeListener((buttonView, isChecked) -> {
            databaseReference
                    .child(user.getUserID())
                    .child("userAdmin")
                    .setValue(isChecked);
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        public TextView userName, emailTV;
        @SuppressLint("UseSwitchCompatOrMaterialCode")
        public Switch userIsAdmin;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            userIsAdmin = itemView.findViewById(R.id.btnisAdmin);
            userName = itemView.findViewById(R.id.userName);
            emailTV = itemView.findViewById(R.id.emailTV);
        }
    }

}
