package com.example.suzzy;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.suzzy.FragmentAdapter.ExploreandShopAdapter;
import com.example.suzzy.FragmentAdapter.topCategoryAdapter;
import com.example.suzzy.FragmentAdapter.topItemsAdapter;
import com.example.suzzy.FragmentListClasses.ExploreandShopList;
import com.example.suzzy.FragmentListClasses.topCategoryList;
import com.example.suzzy.FragmentListClasses.topItemsList;

import com.example.suzzy.GeneralClasses.General;
import com.example.suzzy.MainFrags.CartFrag;
import com.example.suzzy.MainFrags.MoreFrag;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    BottomNavigationView bottomNavigationView;
    private DatabaseReference mDatabase;
    CoordinatorLayout snackbar;
    private ExploreandShopAdapter adapter;
    private topCategoryAdapter categoryAdapter;
    private topItemsAdapter itemsAdapter;
    private List<topItemsList> topItemsLists;
    private List<ExploreandShopList> toplist;
    private List<topCategoryList> categoryList;
    private RecyclerView exploreRecycler, topcategoryRecyclerview, topitemsRecyclerview;
    boolean OndoubleBackpressed = false;
    CoordinatorLayout mainsnack;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Fresco.initialize(this);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        //end of firebase settings call
        //view initialisers
        initExploreandShop();
        initTopCategory();
        initTopItems();
        //view initialisation

        //data loading functions and handlers
        LoadExploreandShopData();
        LoadtopItemsList();
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.getMenu().findItem(R.id.bottom_nav_home).setChecked(true);
        mainsnack = findViewById(R.id.main_snackbar);


    }

    private void initExploreandShop() {
        toplist = new ArrayList<>();
        exploreRecycler = findViewById(R.id.top_category_recyclerview);
        exploreRecycler.setHasFixedSize(true);
        exploreRecycler.setNestedScrollingEnabled(false);
        exploreRecycler.setLayoutManager(new GridLayoutManager(MainActivity.this,
                2, GridLayoutManager.HORIZONTAL, false));
        adapter = new ExploreandShopAdapter(MainActivity.this, toplist);
        exploreRecycler.setAdapter(adapter);
    }

    void initTopCategory() {
        categoryList = new ArrayList<>();
        topcategoryRecyclerview = findViewById(R.id.explore_recyclerview);
        topcategoryRecyclerview.setNestedScrollingEnabled(false);
        topcategoryRecyclerview.setLayoutManager(new GridLayoutManager(MainActivity.this, 2, GridLayoutManager.VERTICAL, false));
        categoryAdapter = new topCategoryAdapter(MainActivity.this, categoryList);
        topcategoryRecyclerview.setAdapter(categoryAdapter);


    }
    void initTopItems(){
        topItemsLists = new ArrayList<>();
        topitemsRecyclerview = findViewById(R.id.topitems_recyclerview);
        topitemsRecyclerview.setLayoutManager(new GridLayoutManager(MainActivity.this,
                2, GridLayoutManager.VERTICAL, false));
        topitemsRecyclerview.setNestedScrollingEnabled(false);
        itemsAdapter = new topItemsAdapter(MainActivity.this, topItemsLists);
        topitemsRecyclerview.setAdapter(itemsAdapter);

    }
    void LoadtopItemsList(){
        DatabaseReference products = mDatabase.child("products");
        products.keepSynced(true);
        products.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                topItemsLists.clear();
                for (DataSnapshot product:dataSnapshot.getChildren()
                ) {
                    if(product.child("topitem").exists()){
                        topItemsList list = product.getValue(topItemsList.class);
                        topItemsLists.add(list);
                        itemsAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //Snackbar.make(snackbar, databaseError.getMessage(), BaseTransientBottomBar.LENGTH_SHORT).show();
            }
        });
    }

    void LoadExploreandShopData() {
        DatabaseReference category = mDatabase.child("category");
        category.keepSynced(true);
        category.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                toplist.clear();
                categoryList.clear();
                for (DataSnapshot category : dataSnapshot.getChildren()
                ) {
                    ExploreandShopList exploreandShopList = category.getValue(ExploreandShopList.class);

                    toplist.add(exploreandShopList);
                    adapter.notifyDataSetChanged();

                    if (category.child("topcategory").exists()) {
                        topCategoryList list = category.getValue(com.example.suzzy.FragmentListClasses.topCategoryList.class);
                        categoryList.add(list);
                        categoryAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //Snackbar.make(snackbar, databaseError.getMessage(), BaseTransientBottomBar.LENGTH_SHORT).show();
            }
        });
    }
public static boolean isLogged(){
        return(FirebaseAuth.getInstance().getCurrentUser() != null?true:false);
}
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        //Menu menu = bottomNavigationView.getMenu();
        switch(item.getItemId()){
            case R.id.bottom_nav_cart:
                if(isLogged()) startActivity(new Intent(MainActivity.this, CartFrag.class));
                else new General().openAccountCreation(this);
                break;
            case R.id.bottom_nav_home:
                startActivity(new Intent(MainActivity.this,MainActivity.class))  ;
                break;
            case R.id.bottom_nav_more:
                startActivity(new Intent(MainActivity.this, MoreFrag.class))  ;
                break;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if(OndoubleBackpressed){
         moveTaskToBack(true);
        }
        OndoubleBackpressed = true;
        Toast.makeText(MainActivity.this, "Click again to exit", Toast.LENGTH_SHORT).show();
      new Handler().postDelayed(new Runnable() {
          @Override
          public void run() {
              //do somethin after sleeping for 2000 milliseconds
              OndoubleBackpressed = false;

          }
      }, 2000);
    }
}
