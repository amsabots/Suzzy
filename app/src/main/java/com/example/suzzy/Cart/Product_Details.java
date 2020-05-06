package com.example.suzzy.Cart;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.suzzy.R;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class Product_Details extends AppCompatActivity {
    ViewPager viewpager;
    RecyclerView recyclerView;
    LinearLayout mdots;
    TextView[] dots;
    ImagesSample adapter;
    List<ImagesList> imagesLists;
    String category_id, item_id;
    private static final String TAG = "Product_Details";
    TextView tag, price, name, desc, size, additional_info, unit;
    RecyclerView item_detail_recycler;
    LinearLayout item_tag_info, dotslayout;
    ConstraintLayout related_products;
    Button add_to_cart;
    List<ProductList> productList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product__details);
        //getting the sent bundle from category and cart activity
        category_id = getIntent().getStringExtra("category");
        item_id = getIntent().getStringExtra("item");
        Log.d(TAG, "onCreate: " + category_id + " " + item_id);
        //initialising the sooooo many views in the xml layou
        initViews();
        setupviewPager();
        //init the productList
        productList = new ArrayList<>();
        //init the related items recycler
        item_detail_recycler.setHasFixedSize(true);
        item_detail_recycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,
                false));


    }

    void setupviewPager() {
        viewpager = findViewById(R.id.product_item_viewpager);
        imagesLists = new ArrayList<>();
        adapter = new ImagesSample(imagesLists, this);
        viewpager.setAdapter(adapter);
    }


    void initViews() {
        tag = findViewById(R.id.item_detail_tagname);
        size = findViewById(R.id.item_detail_size);
        price = findViewById(R.id.item_detail_price);
        unit = findViewById(R.id.item_detail_unit);
        desc = findViewById(R.id.item_detail_desc);
        additional_info = findViewById(R.id.item_detail_additional_info);
        name = findViewById(R.id.item_detail_name);
        add_to_cart = findViewById(R.id.item_detail_add_to_cart_btn);
        item_detail_recycler = findViewById(R.id.item_detail_recycler_view);
        item_tag_info = findViewById(R.id.item_detail_tag_container);
        dotslayout = findViewById(R.id.dots_layout);
        related_products = findViewById(R.id.item_detail_related_recycler_view);
    }

    // Viewpager Class
    public class ImagesSample extends PagerAdapter {
        List<ImagesList> list;
        Context context;

        public ImagesSample(List<ImagesList> list, Context context) {
            this.list = list;
            this.context = context;
        }

        @Override
        public int getCount() {
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

    //imagesList class
    public class ImagesList {
        String images;

        public ImagesList() {
        }

        public ImagesList(String images) {
            this.images = images;
        }

        public String getImages() {
            return images;
        }

        public void setImages(String images) {
            this.images = images;
        }
    }
}
