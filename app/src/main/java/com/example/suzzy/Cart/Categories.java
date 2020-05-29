package com.example.suzzy.Cart;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.suzzy.GeneralClasses.General;
import com.example.suzzy.InterfaceMethods.IfireBase;
import com.example.suzzy.ItemFilter;
import com.example.suzzy.MainActivity;
import com.example.suzzy.MainFrags.CartFrag;
import com.example.suzzy.R;
import com.example.suzzy.Search;
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
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.example.suzzy.MainActivity.CATEGORY_ID;
import static com.example.suzzy.MainActivity.CATEGORY_TYPE;
import static com.example.suzzy.MainActivity.ITEM_ID;
import static com.example.suzzy.MainActivity.ITEM_NAME;


public class Categories extends AppCompatActivity implements Categories_Adapter.OnItemClickListener {
    private RecyclerView productsRecyclerv;
    private static final String TAG = "Categories";
    private List<ProductList> list;
    TextView textCartItemCount, location, change_location;
    MaterialToolbar toolbar;
    long count;
    FrameLayout cart_icon_custom;
    CoordinatorLayout snack;
    Categories_Adapter adapter;
    String type, categoryid, id;
    ProgressDialog progressDialog;
    int itemsSize;
    EditText search;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);
        list = new ArrayList<>();
        adapter = new Categories_Adapter(list, this);
        //Log.i(TAG, "onCreate: " + getIntent().getStringExtra("category") + getIntent().getStringExtra("category_id"));
        type = getIntent().getStringExtra(CATEGORY_TYPE);
        categoryid = getIntent().getStringExtra(CATEGORY_ID);
        productsRecyclerv = findViewById(R.id.category_list_recyclerview);
        productsRecyclerv.setHasFixedSize(true);
        productsRecyclerv.setAdapter(adapter);
        setSupportActionBar(toolbar);
        progressDialog = new ProgressDialog(this);
        setUpBadge();
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
        LoadData(type, categoryid);
        adapter.setOnItemclickListener(this);

        location = findViewById(R.id.category_location);
        change_location = findViewById(R.id.category_change_location);

        location.setText(General.getResidence());
        change_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new General().openEditLocation(Categories.this);
            }
        });
        getUplocation();

        search = findViewById(R.id.category_search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Categories.this, Search.class));
            }
        });
    }

    void getUplocation() {
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
                    new General().openEditLocation(Categories.this);
                else new General().openEditLocation(Categories.this);
            }
        });
    }


    private void LoadData(final String type, String categoryid) {
        progressDialog.setMessage("Please wait.........");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        Query mainref = null;
        if (type.equals("all")) {
            Log.i(TAG, "LoadData: CATEGORY_TYPE IS ALL");
            mainref = FirebaseDatabase.getInstance().getReference().child("products");
        } else {
            Log.i(TAG, "LoadData: CATEGORY_TYPE IS CATEGORISED");
            mainref = FirebaseDatabase.getInstance().getReference().child("products")
                    .orderByChild("categoryid").equalTo(categoryid);
        }
        mainref.keepSynced(true);

        mainref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                progressDialog.dismiss();
                if (dataSnapshot.exists()) {
                    for (final DataSnapshot item : dataSnapshot.getChildren()
                    ) {
                        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                            Query mref = FirebaseDatabase.getInstance().getReference()
                                    .child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .child("Cart").orderByChild("id").equalTo(item.child("id").getValue().toString());
                            mref.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (!dataSnapshot.exists()) {
                                        if (!type.equals("item")) {
                                            ProductList productList = item.getValue(ProductList.class);
                                            list.add(productList);
                                            adapter.notifyDataSetChanged();
                                        } else {
                                            ProductList productList = item.getValue(ProductList.class);
                                            list.add(productList);
                                            List<ProductList> filteredList = Lists.newArrayList(Collections2.filter(list,
                                                    new ItemFilter(getIntent()
                                                            .getStringExtra(ITEM_NAME).toLowerCase())));
                                            list.clear();
                                            list.addAll(filteredList);
                                            adapter.notifyDataSetChanged();
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    if (!type.equals("item")) {
                                        ProductList productList = item.getValue(ProductList.class);
                                        list.add(productList);
                                        adapter.notifyDataSetChanged();
                                    } else {
                                        ProductList productList = item.getValue(ProductList.class);
                                        list.add(productList);
                                        List<ProductList> filteredList = Lists.newArrayList(Collections2.filter(list,
                                                new ItemFilter(getIntent()
                                                        .getStringExtra(ITEM_NAME).toLowerCase())));
                                        list.clear();
                                        list.addAll(filteredList);
                                        adapter.notifyDataSetChanged();
                                    }


                                }
                            });

                        } else {
                            if (!type.equals("item")) {
                                ProductList productList = item.getValue(ProductList.class);
                                list.add(productList);
                                adapter.notifyDataSetChanged();
                            } else {
                                ProductList productList = item.getValue(ProductList.class);
                                list.add(productList);
                                List<ProductList> filteredList = Lists.newArrayList(Collections2.filter(list,
                                        new ItemFilter(getIntent()
                                                .getStringExtra(ITEM_NAME).toLowerCase())));
                                list.clear();
                                list.addAll(filteredList);
                                adapter.notifyDataSetChanged();
                            }
                        }

                    }
                } else {
                    new MaterialAlertDialogBuilder(Categories.this)
                            .setTitle("OOOOps!!! We are out of stock")
                            .setMessage("Seems like the products under this Category are out of stock/n Continue shopping")
                            .setIcon(R.drawable.ic_info_black_24dp)
                            .setPositiveButton("Back to Shopping", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    startActivity(new Intent(Categories.this, MainActivity.class));
                                }
                            }).setCancelable(false).show();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


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
                    Log.d(TAG, "onDataChange: " + String.valueOf(count));
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
    public void onLikedClikcked(int position) {

    }

    @Override
    public void onAddItemClicked(int position) {
        saveToUserCart(position);
    }

    @Override
    public void openDetailsClicked(int position) {
        Intent intent = new Intent(Categories.this, Product_Details.class);
        intent.putExtra(CATEGORY_ID, list.get(position).getCategoryid());
        intent.putExtra(ITEM_ID, list.get(position).getId());
        startActivity(intent);
    }


    public void saveToUserCart(final int position) {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            FirebaseDatabase.getInstance().getReference().child("Users")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child("Cart").orderByChild("id").equalTo(list.get(position).getId())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (!dataSnapshot.exists()) {
                                progressDialog.setCanceledOnTouchOutside(false);
                                progressDialog.show();
                                list.get(position).setLoading(true);
                                adapter.notifyDataSetChanged();
                                if (!list.get(position).isSavedinCart()) {
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
                                                progressDialog.dismiss();
                                                Snackbar.make(snack, "added to cart", BaseTransientBottomBar.LENGTH_SHORT)
                                                        .setDuration(500)
                                                        .show();
                                                list.remove(position);
                                                adapter.notifyItemRemoved(position);
                                                adapter.notifyItemRangeChanged(position, list.size());
                                                ;
                                            } else {
                                                progressDialog.dismiss();
                                                Toast.makeText(Categories.this, "Task failed, Please try again", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            progressDialog.dismiss();
                                            Snackbar.make(snack, "Task failed, Please try again", BaseTransientBottomBar.LENGTH_SHORT)
                                                    .show();
                                        }
                                    });
                                } else {
                                    progressDialog.dismiss();
                                    Snackbar.make(snack, "item already in your cart", BaseTransientBottomBar.LENGTH_SHORT)
                                            .setDuration(500)
                                            .show();
                                }

                            } else {
                                Snackbar.make(snack, "item already in your cart", BaseTransientBottomBar.LENGTH_SHORT)
                                        .show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        } else {
            new General().openAccountCreation(Categories.this);
        }


    }


//    void updateItemList(int id){
//        Log.i(TAG, "updateItemList: list size: "+id);
//        if(id > 0){
//            for (int i = 0; i < id; i++){
//                ProductList checkList = list.get(i);
//                Query query = FirebaseDatabase.getInstance().getReference()
//                        .child("Users").child("Cart").orderByChild("id").equalTo(checkList.getId());
//                final int finalI = i;
//                query.addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                      if (dataSnapshot.exists()){
//                          list.get(finalI).setSavedinCart(true);
//                          adapter.notifyDataSetChanged();
//                      }else{
//                          list.get(finalI).setSavedinCart(false);
//                          adapter.notifyDataSetChanged();
//                      }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });
//            }
    //     }
    // }

}
