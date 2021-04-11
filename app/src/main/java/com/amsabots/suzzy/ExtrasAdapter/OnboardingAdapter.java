package com.amsabots.suzzy.ExtrasAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amsabots.suzzy.R;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.PagerAdapter;

public class OnboardingAdapter extends PagerAdapter {
    public OnboardingAdapter(Context context) {
        this.context = context;
    }

    Context context;
    LayoutInflater layoutInflater;
public int[] background = {
        R.mipmap.location,
        R.mipmap.cart,
        R.mipmap.more
};

    @Override
    public int getCount() {
        return background.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (ConstraintLayout)object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.onboarding_slider_layout, container, false);
        ConstraintLayout constraintLayout = view.findViewById(R.id.onboarding_background_images);
        constraintLayout.setBackgroundResource(background[position]);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((ConstraintLayout)object);
    }
}
