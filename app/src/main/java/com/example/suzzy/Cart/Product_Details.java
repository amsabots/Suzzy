package com.example.suzzy.Cart;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.suzzy.R;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

public class Product_Details extends AppCompatActivity {
    ViewPager viewpager;
    RecyclerView recyclerView;
    LinearLayout mdots;
    TextView[] dots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product__details);
    }
}
