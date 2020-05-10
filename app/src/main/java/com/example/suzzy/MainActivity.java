package com.example.suzzy;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.suzzy.Cart.Categories;
import com.example.suzzy.Cart.Product_Details;
import com.example.suzzy.FragmentAdapter.ExploreandShopAdapter;
import com.example.suzzy.FragmentAdapter.topCategoryAdapter;
import com.example.suzzy.FragmentAdapter.topItemsAdapter;
import com.example.suzzy.FragmentListClasses.ExploreandShopList;
import com.example.suzzy.FragmentListClasses.topCategoryList;
import com.example.suzzy.FragmentListClasses.topItemsList;

import com.example.suzzy.GeneralClasses.General;
import com.example.suzzy.MainFrags.CartFrag;
import com.example.suzzy.MainFrags.MoreFrag;
import com.example.suzzy.Preferences.OnBoardingScreen;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
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

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener,
        topItemsAdapter.OnTopItemCardClickListener, topCategoryAdapter.CategoryCardClickListener,
        ExploreandShopAdapter.ExploreandShopCardClickListener {
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
    TextView location, change_location;
    private static final String TAG = "MainActivity";
    LinearLayout loader;
    SpinKitView loading;
    MaterialButton reload;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Fresco.initialize(this);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        //end of firebase settings call
        //view initialisers
        if(OnBoardingScreen.getInstance(this).isNew()){
            startActivity(new Intent(this, OnboardingScreen.class));
        }
        initExploreandShop();
        initTopCategory();
        initTopItems();
        //view initialisation
        loader = findViewById(R.id.loading_screen);
        loading = findViewById(R.id.main_activity_loader);
        reload = findViewById(R.id.main_activity_refresh);
        //data loading functions and handlers
        LoadExploreandShopData();
        LoadtopItemsList();
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.getMenu().findItem(R.id.bottom_nav_home).setChecked(true);
        mainsnack = findViewById(R.id.main_snackbar);
        change_location = findViewById(R.id.main_change_location);
        location = findViewById(R.id.main_location);
        //set default location in main toolbar
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            String USERID = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference mref = FirebaseDatabase.getInstance().getReference().child("Users")
                    .child(USERID).child("location");
            mref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                       location.setText(dataSnapshot.child("residence").getValue().toString()+", "+
                               dataSnapshot.child("city").getValue().toString());
                        Log.i(TAG, "onCreate: Location "+ dataSnapshot.toString());
                    }else  location.setText("Not set");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    location.setText("Not set");
                }
            });
        }  location.setText("Not set");
        //end of location fetching

        change_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             if(FirebaseAuth.getInstance().getCurrentUser() == null) new General().openEditLocation(MainActivity.this);
             else new General().openEditLocation(MainActivity.this);
            }
        });


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
        adapter.setOnExploreandShopClickListener(this);
    }

    void initTopCategory() {
        categoryList = new ArrayList<>();
        topcategoryRecyclerview = findViewById(R.id.explore_recyclerview);
        topcategoryRecyclerview.setNestedScrollingEnabled(false);
        topcategoryRecyclerview.setLayoutManager(new GridLayoutManager(MainActivity.this, 2, GridLayoutManager.VERTICAL, false));
        categoryAdapter = new topCategoryAdapter(MainActivity.this, categoryList);
        topcategoryRecyclerview.setAdapter(categoryAdapter);
        categoryAdapter.setOnCategoryCardClickListener(this);


    }
    void initTopItems(){
        topItemsLists = new ArrayList<>();
        topitemsRecyclerview = findViewById(R.id.topitems_recyclerview);
        topitemsRecyclerview.setLayoutManager(new GridLayoutManager(MainActivity.this,
                2, GridLayoutManager.VERTICAL, false));
        topitemsRecyclerview.setNestedScrollingEnabled(false);
        itemsAdapter = new topItemsAdapter(MainActivity.this, topItemsLists);
        topitemsRecyclerview.setAdapter(itemsAdapter);
        itemsAdapter.setOnCardclickListener(this);

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
        loader.setVisibility(View.VISIBLE);
        DatabaseReference category = mDatabase.child("category");
        category.keepSynced(true);
        category.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                loader.setVisibility(View.GONE);
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
                loader.setVisibility(View.GONE);
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

    @Override
    public void onTopItemClick(int position) {
        Intent intent = new Intent(MainActivity.this,Product_Details.class);
        intent.putExtra("category", topItemsLists.get(position).getCategoryid());
        intent.putExtra("item", topItemsLists.get(position).getId());
        startActivity(intent);

    }

    @Override
    public void onCategoryCardClick(int position) {
        Intent intent = new Intent(MainActivity.this, Categories.class);
        intent.putExtra("categoryid", categoryList.get(position).getCategoryID());
        intent.putExtra("type", "category");
        startActivity(intent);
    }

    @Override
    public void ExploreShopClick(int position) {
        Intent intent = new Intent(MainActivity.this, Categories.class);
        intent.putExtra("categoryid", toplist.get(position).getCategoryID());
        intent.putExtra("type", "category");
        startActivity(intent);
    }
}
