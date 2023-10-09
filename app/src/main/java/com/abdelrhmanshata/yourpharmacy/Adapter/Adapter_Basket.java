package com.abdelrhmanshata.yourpharmacy.Adapter;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.abdelrhmanshata.yourpharmacy.Model.ModelItemCart;
import com.abdelrhmanshata.yourpharmacy.R;

import java.util.List;

public class Adapter_Basket extends RecyclerView.Adapter<Adapter_Basket.ItemViewHolder> {

    private final Context mContext;
    private final List<ModelItemCart> carts;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference referenceBasket = database.getReference("Basket");
    private boolean isBasket = false;

    public Adapter_Basket(Context mContext, List<ModelItemCart> carts, boolean isBasket) {
        this.mContext = mContext;
        this.carts = carts;
        this.isBasket = isBasket;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_item_basket, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {

        ModelItemCart cart = carts.get(position);

        holder.numOfMedicineTV.setText(String.valueOf(cart.getNumOfMedicine()));
        holder.medicineName.setText(cart.getMedicineName());
        holder.totalPrice.setText(String.valueOf(cart.getTotalPrice()));

        GradientDrawable colorCircle = (GradientDrawable) holder.numOfMedicineTV.getBackground();
        int CircleColor = getIdColor(position + 1);
        colorCircle.setColor(CircleColor);

        if (isBasket)
            holder.clear.setOnClickListener(v -> deleteItem(cart));

    }

    private void deleteItem(ModelItemCart cart) {
        referenceBasket
                .child(user.getUid())
                .child(cart.getID())
                .removeValue();
    }

    @Override
    public int getItemCount() {
        return carts.size();
    }

    private int getIdColor(int index) {
        int ColorResourceId;
        switch (index % 10) {
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

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        public TextView numOfMedicineTV, medicineName, totalPrice;
        ImageView clear;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            numOfMedicineTV = itemView.findViewById(R.id.numOfMedicineTV);
            medicineName = itemView.findViewById(R.id.medicineName);
            totalPrice = itemView.findViewById(R.id.priceTV);
            clear = itemView.findViewById(R.id.clear);

            if (isBasket)
                clear.setVisibility(View.VISIBLE);
            else
                clear.setVisibility(View.INVISIBLE);
        }
    }
}
