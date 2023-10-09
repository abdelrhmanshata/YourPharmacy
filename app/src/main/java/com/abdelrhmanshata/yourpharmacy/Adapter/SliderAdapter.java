package com.abdelrhmanshata.yourpharmacy.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.abdelrhmanshata.yourpharmacy.R;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.util.List;

public class SliderAdapter extends SliderViewAdapter<SliderAdapter.MyViewholder> {
    List<Integer> imageList;

    public SliderAdapter(List<Integer> imageList) {
        this.imageList = imageList;
    }

    @Override
    public MyViewholder onCreateViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_item, parent, false);
        return new MyViewholder(view);
    }

    @Override
    public void onBindViewHolder(MyViewholder viewHolder, int position) {
        viewHolder.imageView.setImageResource(imageList.get(position));
    }

    @Override
    public int getCount() {
        return imageList.size();
    }

    static class MyViewholder extends SliderViewAdapter.ViewHolder {
        ImageView imageView;

        public MyViewholder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}
