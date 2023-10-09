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

import com.abdelrhmanshata.yourpharmacy.Model.ModelOrder;
import com.abdelrhmanshata.yourpharmacy.R;

import java.util.List;

public class Adapter_Orders extends RecyclerView.Adapter<Adapter_Orders.ItemViewHolder> {

    private final Context mContext;
    private final List<ModelOrder> orders;
    private OnItemClickListener mListener;

    public Adapter_Orders(Context mContext, List<ModelOrder> orders) {
        this.mContext = mContext;
        this.orders = orders;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_item_orders, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {

        GradientDrawable colorCircle;

        ModelOrder order = orders.get(position);

        holder.userName.setText(order.getUserName());
        holder.userPhone.setText(order.getUserPhone());
        holder.userAddress.setText(order.getUserAddress());

        holder.dateOrder.setText(order.getDate());
        holder.typePayment.setText(order.getTypePayment());
        holder.timeOrder.setText(order.getTime());

        holder.InProcessTV.setText("1");
        colorCircle = (GradientDrawable) holder.InProcessTV.getBackground();
        colorCircle.setColor(getIdColor(order.isInProcess()));

        holder.InRoadTV.setText("2");
        colorCircle = (GradientDrawable) holder.InRoadTV.getBackground();
        colorCircle.setColor(getIdColor(order.isInRoad()));
        holder.viewPR.setBackgroundColor(getIdColor(order.isInRoad()));

        holder.DeliveredTV.setText("3");
        colorCircle = (GradientDrawable) holder.DeliveredTV.getBackground();
        colorCircle.setColor(getIdColor(order.isDelivered()));
        holder.viewRD.setBackgroundColor(getIdColor(order.isDelivered()));

    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    private int getIdColor(boolean isDone) {
        if (isDone)
            return ContextCompat.getColor(mContext, R.color.colorLime);
        else
            return ContextCompat.getColor(mContext, R.color.colorGray);
    }

    public interface OnItemClickListener {
        void onItem_Click(int position);
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        public TextView userName, userPhone, userAddress;
        public TextView InProcessTV, InRoadTV, DeliveredTV, dateOrder, typePayment, timeOrder;
        public View viewPR, viewRD;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.userName);
            userPhone = itemView.findViewById(R.id.userPhone);
            userAddress = itemView.findViewById(R.id.userAddress);

            InProcessTV = itemView.findViewById(R.id.InProcessTV);
            InRoadTV = itemView.findViewById(R.id.InRoadTV);
            DeliveredTV = itemView.findViewById(R.id.DeliveredTV);
            dateOrder = itemView.findViewById(R.id.dateOrder);
            typePayment = itemView.findViewById(R.id.typePayment);
            timeOrder = itemView.findViewById(R.id.timeOrder);
            viewPR = itemView.findViewById(R.id.viewPR);
            viewRD = itemView.findViewById(R.id.viewRD);

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

}
