package com.abdelrhmanshata.yourpharmacy.Adapter;

import android.annotation.SuppressLint;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.abdelrhmanshata.yourpharmacy.Model.ModelItemCart;
import com.abdelrhmanshata.yourpharmacy.Model.ModelMedicine;
import com.abdelrhmanshata.yourpharmacy.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import es.dmoral.toasty.Toasty;

public class Adapter_U_Medicines extends ArrayAdapter<ModelMedicine> {

    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference referenceBasket = database.getReference("Basket");

    Context context;
    int numberOfMedicine = 1;

    public Adapter_U_Medicines(@NonNull Context context, @NonNull List<ModelMedicine> objects) {
        super(context, 0, objects);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item_u_medicine, parent, false);
        }

        ModelMedicine medicine = getItem(position);

        ImageView MedicineImage = listItemView.findViewById(R.id.MedicineImage);
        ImageView ImageIsDiscount = listItemView.findViewById(R.id.imageIsDiscount);
        TextView MedicineName = listItemView.findViewById(R.id.MedicineName);
        TextView MedicinePrice = listItemView.findViewById(R.id.MedicinePrice);
        TextView PriceDiscount = listItemView.findViewById(R.id.MedicinePriceDiscount);
        RelativeLayout layout = listItemView.findViewById(R.id.addCart);

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
        if (medicine.isDiscount()) {
            ImageIsDiscount.setVisibility(View.VISIBLE);
            PriceDiscount.setVisibility(View.VISIBLE);
            String discountPrice = (medicine.getMedicinePrice() - medicine.getMedicineDiscount()) + " " + context.getResources().getString(R.string.currency);
            PriceDiscount.setText(discountPrice);
            String price = medicine.getMedicinePrice() + " " + context.getResources().getString(R.string.currency);
            SpannableString spannableString = new SpannableString(price);
            StrikethroughSpan span = new StrikethroughSpan();
            spannableString.setSpan(span, 0, price.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            MedicinePrice.setText(spannableString);
        } else {
            ImageIsDiscount.setVisibility(View.GONE);
            PriceDiscount.setVisibility(View.GONE);
            String price = medicine.getMedicinePrice() + " " + context.getResources().getString(R.string.currency);
            MedicinePrice.setText(price);
        }

        layout.setOnClickListener(v -> showDialogAddToCart(medicine));

        return listItemView;
    }

    @SuppressLint("SetTextI18n")
    public void showDialogAddToCart(ModelMedicine medicine) {
        numberOfMedicine = 1;
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(getContext()).inflate(
                R.layout.layout_add_to_cart, null, false);
        dialogBuilder.setView(dialogView);
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
        //
        TextView MedicineName = dialogView.findViewById(R.id.MedicineName);
        TextView MedicinePrice = dialogView.findViewById(R.id.MedicinePrice);
        TextView MedicinePriceDiscount = dialogView.findViewById(R.id.MedicinePriceDiscount);
        TextView totalPrice = dialogView.findViewById(R.id.totalPrice);
        TextView subBtn = dialogView.findViewById(R.id.subBtn);
        TextView numTV = dialogView.findViewById(R.id.numTV);
        TextView addBtn = dialogView.findViewById(R.id.addBtn);
        RelativeLayout layout = dialogView.findViewById(R.id.addCart);
        //
        MedicineName.setText(medicine.getMedicineName());
        if (medicine.isDiscount()) {
            MedicinePriceDiscount.setVisibility(View.VISIBLE);
            String discountPrice = (medicine.getMedicinePrice() - medicine.getMedicineDiscount()) + " " + context.getResources().getString(R.string.currency);
            MedicinePriceDiscount.setText(discountPrice);
            String price = medicine.getMedicinePrice() + " " + context.getResources().getString(R.string.currency);
            SpannableString spannableString = new SpannableString(price);
            StrikethroughSpan span = new StrikethroughSpan();
            spannableString.setSpan(span, 0, price.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            MedicinePrice.setText(spannableString);
        } else {
            MedicinePriceDiscount.setVisibility(View.GONE);
            String price = medicine.getMedicinePrice() + " " + context.getResources().getString(R.string.currency);
            MedicinePrice.setText(price);
        }

        numTV.setText(String.valueOf(numberOfMedicine));
        totalPrice.setText(getTotalPrice(medicine) + " " + context.getResources().getString(R.string.currency));

        //
        addBtn.setOnClickListener(v -> {
            numberOfMedicine++;
            numTV.setText(String.valueOf(numberOfMedicine));
            totalPrice.setText(getTotalPrice(medicine) + " " + context.getResources().getString(R.string.currency));
        });
        subBtn.setOnClickListener(v -> {
            if (numberOfMedicine > 1) {
                numberOfMedicine--;
                numTV.setText(String.valueOf(numberOfMedicine));
                totalPrice.setText(getTotalPrice(medicine) + " " + context.getResources().getString(R.string.currency));
            }
        });

        layout.setOnClickListener(v -> {
            ModelItemCart itemCart = new ModelItemCart();
            itemCart.setID(medicine.getMedicineID());
            itemCart.setMedicineName(medicine.getMedicineName());
            itemCart.setNumOfMedicine(numberOfMedicine);
            itemCart.setTotalPrice(getTotalPrice(medicine));
            referenceBasket
                    .child(user.getUid())
                    .child(itemCart.getID())
                    .setValue(itemCart)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toasty.success(context, context.getResources().getString(R.string.Added_successfully), Toast.LENGTH_SHORT).show();
                            alertDialog.dismiss();
                        } else {
                            Toasty.error(context, "Error", Toast.LENGTH_SHORT).show();
                            alertDialog.dismiss();
                        }
                    });
        });
    }

    public double getTotalPrice(ModelMedicine m) {
        if (m.isDiscount()) {
            double price = m.getMedicinePrice() - m.getMedicineDiscount();
            double total = numberOfMedicine * price;
            return total;
        } else {
            double price = m.getMedicinePrice();
            double total = numberOfMedicine * price;
            return total;
        }
    }
}
