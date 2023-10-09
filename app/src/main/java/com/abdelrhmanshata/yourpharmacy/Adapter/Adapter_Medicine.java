package com.abdelrhmanshata.yourpharmacy.Adapter;

import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.abdelrhmanshata.yourpharmacy.Model.ModelMedicine;
import com.abdelrhmanshata.yourpharmacy.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class Adapter_Medicine extends ArrayAdapter<ModelMedicine> {
    Context context;

    public Adapter_Medicine(@NonNull Context context, @NonNull List<ModelMedicine> objects) {
        super(context, 0, objects);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item_medicine, parent, false);
        }

        ModelMedicine medicine = getItem(position);

        ImageView MedicineImage = listItemView.findViewById(R.id.MedicineImage);
        ImageView ImageIsDiscount = listItemView.findViewById(R.id.imageIsDiscount);
        TextView MedicineName = listItemView.findViewById(R.id.MedicineName);
        TextView MedicinePrice = listItemView.findViewById(R.id.MedicinePrice);
        TextView PriceDiscount = listItemView.findViewById(R.id.MedicinePriceDiscount);

        try {
            Picasso
                    .get()
                    .load(medicine.getMedicineImage().trim() + "")
                    .fit()
                    .placeholder(R.drawable.loading)
                    .into(MedicineImage);
        } catch (Exception e) {
            Log.d("" + context, e.getMessage());
        }
        MedicineName.setText(medicine.getMedicineName());
        if(medicine.isDiscount())
        {
            ImageIsDiscount.setVisibility(View.VISIBLE);
            PriceDiscount.setVisibility(View.VISIBLE);
            String discountPrice = (medicine.getMedicinePrice() - medicine.getMedicineDiscount())+" "+context.getResources().getString(R.string.currency);
            PriceDiscount.setText(discountPrice);
            String price = medicine.getMedicinePrice()+" "+context.getResources().getString(R.string.currency);
            SpannableString spannableString = new SpannableString(price);
            StrikethroughSpan span = new StrikethroughSpan();
            spannableString.setSpan(span,0,price.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            MedicinePrice.setText(spannableString);
        }else{
            ImageIsDiscount.setVisibility(View.GONE);
            PriceDiscount.setVisibility(View.GONE);
            String price = medicine.getMedicinePrice()+" "+context.getResources().getString(R.string.currency);
            MedicinePrice.setText(price);
        }

        return listItemView;
    }
}
