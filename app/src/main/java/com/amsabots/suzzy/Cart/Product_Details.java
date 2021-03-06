package com.amsabots.suzzy.Cart;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amsabots.suzzy.FragmentListClasses.topItemsList;
import com.amsabots.suzzy.GeneralClasses.General;
import com.amsabots.suzzy.MainActivity;
import com.amsabots.suzzy.R;
import com.amsabots.suzzy.Search;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import static com.amsabots.suzzy.MainActivity.CATEGORY_ID;
import static com.amsabots.suzzy.MainActivity.CATEGORY_TYPE;
import static com.amsabots.suzzy.MainActivity.ITEM_ID;
import static com.amsabots.suzzy.MainActivity.ITEM_NAME;

public class Product_Details extends AppCompatActivity implements ProductDetails_Adapter.OnCardItemClickListener,
        View.OnClickListener {
    ViewPager viewpager;
    TextView[] dots;
    ImagesSample adapter;
    String category_id, item_id;
    private static final String TAG = "Product_Details";
    TextView tag, price, name, desc, size, additional_info, unit, location, change_location;
    RecyclerView item_detail_recycler;
    LinearLayout item_tag_info, dotslayout;
    ConstraintLayout related_products;
    Button add_to_cart;
    //list items
    List<ImagesList> imagesLists;
    List<topItemsList> mainItemList;
    ProgressDialog dialogue;
    CoordinatorLayout snack;
    CartList cartList;
    int currentImage, imagessize;
    ProductDetails_Adapter itemsAdapter;
    ImageView arro_back, cancel_back;
    EditText main_searchview;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product__details);
        //getting the sent bundle from category and cart activity
        category_id = getIntent().getStringExtra(CATEGORY_ID);
        item_id = getIntent().getStringExtra(ITEM_ID);
        //initialising the sooooo many views in the xml layout
        initViews();
        //init the productList
        imagesLists = new ArrayList<>();
        dialogue = new ProgressDialog(this);
        snack = findViewById(R.id.detail_snack);
        LoadProductInfo(item_id, category_id);
        setupviewPager();
        getRelatedProducts(item_id, category_id);
        itemsAdapter = new ProductDetails_Adapter(this, mainItemList);
        itemsAdapter.setOnCardClickListener(this);
        item_detail_recycler.setHasFixedSize(true);
        item_detail_recycler.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL,
                false));
        item_detail_recycler.setNestedScrollingEnabled(false);
        item_detail_recycler.setAdapter(itemsAdapter);
        location = findViewById(R.id.product_detail_location);
        change_location = findViewById(R.id.product_detail_change_location);
        setuplocation();
        arro_back = findViewById(R.id.cart_get_to_parent);
        cancel_back = findViewById(R.id.cart_get_to_parent_cancel);
        arro_back.setOnClickListener(this);
        cancel_back.setOnClickListener(this);
        main_searchview = findViewById(R.id.main_searchview);
        main_searchview.setOnClickListener(this);

    }

    void setuplocation() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            String USERID = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference mref = FirebaseDatabase.getInstance().getReference().child("Users")
                    .child(USERID).child("location");
            mref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild("city")) {
                        location.setText(dataSnapshot.child("residence").getValue().toString() + ", " +
                                dataSnapshot.child("city").getValue().toString());
                        Log.i(TAG, "onCreate: Location " + dataSnapshot.toString());
                    } else location.setText("Not set");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    location.setText("Not set");
                }
            });
        }
        location.setText("Not set");
        //end of location fetching

        change_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FirebaseAuth.getInstance().getCurrentUser() == null)
                    new General().openEditLocation(Product_Details.this);
                else new General().openEditLocation(Product_Details.this);
            }
        });
    }

    private void getRelatedProducts(final String id, String category_id) {
        mainItemList = new ArrayList<>();
        Query ref = FirebaseDatabase.getInstance().getReference()
                .child("products")
                .orderByChild("categoryid").equalTo(category_id);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() > 1) {
                    for (DataSnapshot items : dataSnapshot.getChildren()
                    ) {
                        //Log.d(TAG, "onDataChange: items"+ items.toString());
                        if (!items.child("id").getValue().toString().equals(id)) {
                            topItemsList list = items.getValue(topItemsList.class);
                            mainItemList.add(list);
                            itemsAdapter.notifyDataSetChanged();
                        }
                    }
                } else related_products.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                related_products.setVisibility(View.GONE);
            }
        });

    }

    private void LoadProductInfo(final String item_id, final String category_id) {
        dialogue.setMessage("Fetching....");
        dialogue.setCanceledOnTouchOutside(false);
        dialogue.show();
        Query item = FirebaseDatabase.getInstance().getReference().child("products")
                .orderByChild("id").equalTo(item_id);
        item.keepSynced(true);
        item.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dialogue.dismiss();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot product : dataSnapshot.getChildren()
                    ) {
                        ImagesList imagesList = new ImagesList();
                        imagesList.setImages(product.child("imageurl").getValue().toString());
                        imagesLists.add(imagesList);
                        adapter.notifyDataSetChanged();
                        if (product.hasChild("related_images")) {
                            for (DataSnapshot images : product.child("related_images").getChildren()
                            ) {
                                imagesList.setImages(images.child("image").getValue(true).toString());
                                imagesLists.add(imagesList);
                                adapter.notifyDataSetChanged();

                            }
                        }
                        name.setText(product.child("name").getValue().toString());
                        tag.setText(product.hasChild("tag") ? product.child("tag").getValue().toString() : "Express delivery");
                        additional_info.setText(product.hasChild("additiona_info") ?
                                product.child("additional_info").getValue().toString() : "Delivery at the shortest time possible");
                        price.setText(String.valueOf(product.child("price").getValue()));
                        size.setText(product.hasChild("size") ? product.child("size").getValue().toString() : "");
                        unit.setText(product.hasChild("unit") ? "/" + product.child("unit").getValue().toString() : "/unit");
                        desc.setText(product.child("desc").getValue().toString());
                      cartList = product.getValue(CartList.class);


                    }
                    imagessize = imagesLists.size();
                    setDostoCurrentPage(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                dialogue.dismiss();
                Snackbar.make(snack, databaseError.getMessage(), BaseTransientBottomBar.LENGTH_LONG)
                        .setAction("Retry",
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        LoadProductInfo(item_id, category_id);
                                    }
                                }).show();

            }
        });

    }

    void setupviewPager() {
        viewpager = findViewById(R.id.product_item_viewpager);
        adapter = new ImagesSample(imagesLists, this);
        viewpager.setAdapter(adapter);
        viewpager.addOnPageChangeListener(pageChangeListener);
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
//setup and attach adapter to recyclerview
        related_products = findViewById(R.id.item_detail_related_recycler_view);
        add_to_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FirebaseAuth.getInstance().getCurrentUser() != null) saveToUserCart();
                else new General().openAccountCreation(Product_Details.this);
            }
        });
    }

    @Override
    public void onCardItemClick(int position) {
        Intent intent = new Intent(Product_Details.this,Categories.class);
        intent.putExtra(CATEGORY_ID, mainItemList.get(position).getCategoryid());
        intent.putExtra(CATEGORY_TYPE,"item");
        intent.putExtra(ITEM_ID, mainItemList.get(position).getId());
        intent.putExtra(ITEM_NAME, mainItemList.get(position).getName());
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cart_get_to_parent_cancel:
                Intent intent = new Intent(Product_Details.this, Categories.class);
                intent.putExtra(CATEGORY_TYPE, "all");
                startActivity(intent);
                break;
            case R.id.cart_get_to_parent:
                startActivity(new Intent(Product_Details.this, MainActivity.class));
                break;
            case R.id.main_searchview:
                startActivity(new Intent(Product_Details.this, Search.class));
                break;
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


    public void saveToUserCart() {
        dialogue.setMessage("please wait.....");
        dialogue.setCanceledOnTouchOutside(false);
        dialogue.show();
        DatabaseReference cart = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("Cart").push();
        cart.setValue(cartList).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                dialogue.dismiss();
                if (task.isSuccessful()) {
                    add_to_cart.setEnabled(false);
                    add_to_cart.setText("Added to Cart");
                    Snackbar.make(snack, "added to cart", BaseTransientBottomBar.LENGTH_SHORT)
                            .setDuration(500)
                            .show();
                } else {
                    Toast.makeText(Product_Details.this, "Task failed, Please try again", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialogue.dismiss();
                Snackbar.make(snack, "Task failed, Please try again", BaseTransientBottomBar.LENGTH_SHORT)
                        .show();
            }
        });


    }


    ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
          setDostoCurrentPage(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    void setDostoCurrentPage(int position) {
        Log.i(TAG, "setDostoCurrentPage: " + position);
        dots = new TextView[imagessize];
        dotslayout.removeAllViews();
        for (int i = 0; i < imagessize; i++) {
            dots[i] = new TextView(Product_Details.this);
            dots[i].setTextSize(40);
            dots[i].setTextColor(getResources().getColor(R.color.colorPrimary));
            dots[i].setText(Html.fromHtml("&#8226"));
            dotslayout.addView(dots[i]);

//            if(dots.length > 0){
//                dots[position].setTextColor(getResources().getColor(R.color.colorAccent
//                ));
//            }
        }
    }
}

