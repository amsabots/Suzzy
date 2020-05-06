package com.example.suzzy.Cart;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.suzzy.GeneralClasses.General;
import com.example.suzzy.InterfaceMethods.IfireBase;
import com.example.suzzy.MainActivity;
import com.example.suzzy.MainFrags.CartFrag;
import com.example.suzzy.R;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.firebase.ui.database.paging.DatabasePagingOptions;
import com.firebase.ui.database.paging.FirebaseRecyclerPagingAdapter;
import com.firebase.ui.database.paging.LoadingState;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class Categories extends AppCompatActivity {
    Query query;
    private RecyclerView productsRecyclerv;
    private static final String TAG = "Categories";
    FirebaseRecyclerAdapter adapter;
    private List<ProductList> list;
    TextView textCartItemCount;
    MaterialToolbar toolbar;
    long count;
    FrameLayout cart_icon_custom;
    boolean onDoubleBackpressed = false;
    CoordinatorLayout snack;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        Intent intent = getIntent();
        productsRecyclerv = findViewById(R.id.category_list_recyclerview);
        productsRecyclerv.setHasFixedSize(true);
        setSupportActionBar(toolbar);
        setUpBadge();
        Log.d(TAG, "onCreate: " + intent.getStringExtra("categoryid") + " " + intent.getStringExtra("type"));
        productsRecyclerv.setLayoutManager(new LinearLayoutManager(this));
        toolbar = findViewById(R.id.main_toolbar);
        textCartItemCount = findViewById(R.id.cart_badge);
        cart_icon_custom = findViewById(R.id.cart_icon_custom);
        cart_icon_custom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                        startActivity(new Intent(Categories.this, CartFrag.class));
                    } else new General().openAccountCreation(Categories.this);
                }
        });
        snack = findViewById(R.id.category_snack);

    }

    public static class ItemsCategoryAdapter extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tag, price, unit, name, size, save_status, product_add_to_cart;
        LinearLayout save;
        SimpleDraweeView imageview;
        LinearLayout saveTolist;
        ImageView save_status_image;

        public ItemsCategoryAdapter(@NonNull View itemView) {
            super(itemView);
            tag = itemView.findViewById(R.id.product_tag);
            price = itemView.findViewById(R.id.product_price);
            unit = itemView.findViewById(R.id.product_unit);
            name = itemView.findViewById(R.id.product_name);
            size = itemView.findViewById(R.id.product_size);
            save = itemView.findViewById(R.id.product_save_to_list);
            save_status = itemView.findViewById(R.id.product_save_status);
            imageview = itemView.findViewById(R.id.product_image);
            product_add_to_cart = itemView.findViewById(R.id.product_add_to_cart);
            saveTolist = itemView.findViewById(R.id.product_save_to_list);
            save_status_image = itemView.findViewById(R.id.save_status);
            saveTolist.setOnClickListener(this);
            product_add_to_cart.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.product_add_to_cart) {
                listener.onAddClicked(getAdapterPosition());
            } else if (v.getId() == R.id.product_save_to_list) {
                listener.onLikeClicked(getAdapterPosition());
            }
        }

        public interface OnItemClickListener {
            void onAddClicked(int position);

            void onLikeClicked(int index);
        }

        public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
            this.listener = onItemClickListener;
        }

        OnItemClickListener listener;
    }

    @Override
    protected void onStart() {
        super.onStart();
        list = new ArrayList<>();
        query = FirebaseDatabase.getInstance().getReference().child("products");
        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPrefetchDistance(5)
                .setPageSize(20)
                .build();
        FirebaseRecyclerOptions<ProductList> options =
                new FirebaseRecyclerOptions.Builder<ProductList>()
                        .setQuery(query, new SnapshotParser<ProductList>() {
                            @NonNull
                            @Override
                            public ProductList parseSnapshot(@NonNull DataSnapshot snapshot) {
                                return snapshot.getValue(ProductList.class);

                            }
                        })
                        .build();

        adapter = new FirebaseRecyclerAdapter<ProductList, ItemsCategoryAdapter>(options) {
            @Override
            public ItemsCategoryAdapter onCreateViewHolder(ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.category_item_list_cardview, parent, false);

                ItemsCategoryAdapter itemsCategoryAdapter = new ItemsCategoryAdapter(view);
                itemsCategoryAdapter.setOnItemClickListener(new ItemsCategoryAdapter.OnItemClickListener() {
                    @Override
                    public void onAddClicked(int position) {
                        if (FirebaseAuth.getInstance().getCurrentUser() != null)
                            saveToUserCart(position);
                        else
                            new General().openAccountCreation(Categories.this);
                    }

                    @Override
                    public void onLikeClicked(int index) {

                    }
                });
                return itemsCategoryAdapter;
            }

            @Override
            protected void onBindViewHolder(ItemsCategoryAdapter viewHolder, final int position, ProductList model) {
                list.add(model);
                viewHolder.imageview.setImageURI(model.getImageurl());
                viewHolder.name.setText(model.getName());
                viewHolder.price.setText("Kshs " + Long.toString(model.getPrice()) + "/");
                viewHolder.size.setText(model.getSize());
                viewHolder.tag.setVisibility(model.getTag() != null ? View.VISIBLE : View.GONE);
                viewHolder.size.setVisibility(model.getSize() != null ? View.VISIBLE : View.GONE);
                viewHolder.tag.setText(model.getTag());
                viewHolder.unit.setText(model.getUnit());
            }
        };
        productsRecyclerv.setAdapter(adapter);
        adapter.startListening();

//        DatabasePagingOptions<ProductList> options = new DatabasePagingOptions.Builder<ProductList>()
//                .setLifecycleOwner(this)
//                .setQuery(query, config, new SnapshotParser<ProductList>() {
//                    @NonNull
//                    @Override
//                    public ProductList parseSnapshot(@NonNull DataSnapshot snapshot) {
//                        ProductList productList = null;
//                        for (DataSnapshot items : snapshot.getChildren()
//                        ) {
//                            Log.d(TAG, "parseSnapshot: "+items.child("productname")
//                                    .getValue(true).toString());
//
//                            if (items.child("categoryid").getValue(true)
//                                    .toString().equals(getIntent().getStringExtra("categoryid"))) {
//                                productList = items.getValue(ProductList.class);
//                            }
//
//                        }
//                        return productList;
//                    }
//                })
//                .build();
//        adapter  = new FirebaseRecyclerPagingAdapter<ProductList, ItemsCategoryAdapter>(options) {
//            @Override
//            protected void onBindViewHolder(@NonNull ItemsCategoryAdapter viewHolder,
//                                            int position, @NonNull ProductList model) {
//                viewHolder.imageview.setImageURI(model.getImageurl());
//                viewHolder.name.setText(model.getName());
//                viewHolder.price.setText(Long.toString(model.getPrice()));
//                viewHolder.size.setText(model.getSize());
//                viewHolder.tag.setVisibility(model.getTag()!=null?View.GONE:View.VISIBLE);
//                viewHolder.tag.setText(model.getTag());
//                viewHolder.unit.setText(model.getUnit());
//
//            }
//
//            @Override
//            protected void onLoadingStateChanged(@NonNull LoadingState state) {
//
//            }
//
//            @NonNull
//            @Override
//            public ItemsCategoryAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//                View view = LayoutInflater.from(parent.getContext())
//                        .inflate(R.layout.category_item_list_cardview, parent, false);
//                return new ItemsCategoryAdapter(view);
//
//            }
//        };


    }

    private void saveToUserCart(int position) {
        list.get(position).setLoading(true);
        adapter.notifyDataSetChanged();
        DatabaseReference cart = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("Cart").push();
        ProductList p = list.get(position);
        CartList cartList = new CartList(p.getPrice(), 1, System.currentTimeMillis(), p.getUnit(), p.getSize(),
                p.getName(), p.getId(), p.getImageurl(), p.getTag(), p.getCategoryid(), p.getDesc());
        cart.setValue(cartList).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Snackbar.make(snack, "added to cart", BaseTransientBottomBar.LENGTH_SHORT)
                            .setDuration(500)
                            .show();
                } else {
                    Toast.makeText(Categories.this, "Task failed, Please try again", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Snackbar.make(snack, "Task failed, Please try again", BaseTransientBottomBar.LENGTH_SHORT)
                        .show();
            }
        });


    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.cart_icon_menu, menu);
//        //get the menu item
//        final MenuItem menuItem = menu.findItem(R.id.cart_icon_menu);
//        View view = menuItem.getActionView();
//        textCartItemCount = view.findViewById(R.id.cart_badge);
//        setUpBadge();
//        return true;
//    }

    void setUpBadge() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                    .child("Users")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child("Cart");
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    count = dataSnapshot.getChildrenCount();
                    Log.d(TAG, "onDataChange: "+String.valueOf(count));
                    textCartItemCount.setVisibility(count > 0 ? View.VISIBLE : View.GONE);
                    textCartItemCount.setText(String.valueOf(count));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(Categories.this, MainActivity.class));
    }
}
