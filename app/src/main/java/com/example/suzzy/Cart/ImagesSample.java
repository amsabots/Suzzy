package com.example.suzzy.Cart;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.suzzy.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.PagerAdapter;

public  class ImagesSample extends PagerAdapter {
    List<Product_Details.ImagesList> list;
    Context context;
    getPicturesNumber listener;
    public interface getPicturesNumber{
        void returnImagesnumber(int size);
    }
    public void getImagesNumber(getPicturesNumber number){
        this.listener = number;
    }
    public ImagesSample(List<Product_Details.ImagesList> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        if(listener != null){
            listener.returnImagesnumber(list.size());
        }
        return list.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((ConstraintLayout) object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.product_details_images_for_viewpager,
                container, false);
        ImageView imageView = view.findViewById(R.id.viewpager_image_text);
        Glide.with(context)
                .load(list.get(position).getImages())
                .into(imageView);
        container.addView(view);
        return view;

    }
}
