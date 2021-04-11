package com.amsabots.suzzy;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amsabots.suzzy.Cart.Categories;
import com.amsabots.suzzy.FragmentAdapter.ExploreandShopAdapter;
import com.amsabots.suzzy.FragmentAdapter.topCategoryAdapter;
import com.amsabots.suzzy.FragmentAdapter.topItemsAdapter;
import com.amsabots.suzzy.FragmentListClasses.ExploreandShopList;
import com.amsabots.suzzy.FragmentListClasses.topCategoryList;
import com.amsabots.suzzy.FragmentListClasses.topItemsList;

import com.amsabots.suzzy.GeneralClasses.General;
import com.amsabots.suzzy.MainFrags.CartFrag;
import com.amsabots.suzzy.MainFrags.MoreFrag;
import com.amsabots.suzzy.Preferences.OnBoardingScreen;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
    public static final String CATEGORY_ID = "i was sooo dumb back there";
    public static final String ITEM_ID = "then i learnt something";
    public static final String CATEGORY_TYPE = "that Java sometimes sucks";
    public static final String ITEM_NAME = "just keeping up with quarentine";
   EditText searchView;
   ConstraintLayout all_categories, top_categories, top_items;
SwipeRefreshLayout refreshLayout;

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
        refreshLayout = findViewById(R.id.swiperefresh_main);
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
            mref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild("city")) {
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
searchView = findViewById(R.id.main_searchview);
searchView.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
startActivity(new Intent(MainActivity.this, Search.class));
    }
});
reload.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        LoadExploreandShopData();
    }
});
refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
    @Override
    public void onRefresh() {
     LoadExploreandShopData();
     LoadtopItemsList();
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
        all_categories = findViewById(R.id.main_all_categories);
    }

    void initTopCategory() {
        categoryList = new ArrayList<>();
        topcategoryRecyclerview = findViewById(R.id.explore_recyclerview);
        topcategoryRecyclerview.setNestedScrollingEnabled(false);
        topcategoryRecyclerview.setLayoutManager(new GridLayoutManager(MainActivity.this, 2, GridLayoutManager.VERTICAL, false));
        categoryAdapter = new topCategoryAdapter(MainActivity.this, categoryList);
        topcategoryRecyclerview.setAdapter(categoryAdapter);
        categoryAdapter.setOnCategoryCardClickListener(this);
        top_categories = findViewById(R.id.main_top_category);


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
        top_items = findViewById(R.id.main_items);

    }
    void LoadtopItemsList(){
        DatabaseReference products = mDatabase.child("products");
        //products.keepSynced(true);
        products.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                refreshLayout.setRefreshing(false);
                topItemsLists.clear();
                for (DataSnapshot product:dataSnapshot.getChildren()
                ) {
                    if(product.child("topitem").exists()){
                        if(product.child("topitem").getValue(Boolean.class)){
                            topItemsList list = product.getValue(topItemsList.class);
                            topItemsLists.add(list);
                            itemsAdapter.notifyDataSetChanged();
                        }

                    }
                }
                top_items.setVisibility(topItemsLists.size() < 1?View.GONE:View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                refreshLayout.setRefreshing(false);
                //Snackbar.make(snackbar, databaseError.getMessage(), BaseTransientBottomBar.LENGTH_SHORT).show();
            }
        });
    }

    void LoadExploreandShopData() {
        loader.setVisibility(View.VISIBLE);
        DatabaseReference category = mDatabase.child("category");
        //category.keepSynced(true);
        category.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                loader.setVisibility(View.GONE);
                toplist.clear();
                refreshLayout.setRefreshing(false);
                categoryList.clear();
                if(dataSnapshot.exists()){
                    for (DataSnapshot category : dataSnapshot.getChildren()
                    ) {
                        ExploreandShopList exploreandShopList = category.getValue(ExploreandShopList.class);

                        toplist.add(exploreandShopList);
                        adapter.notifyDataSetChanged();

                        if (category.child("topcategory").getValue(Boolean.class)) {
                            topCategoryList list = category.getValue(com.amsabots.suzzy.FragmentListClasses.topCategoryList.class);
                            categoryList.add(list);
                            categoryAdapter.notifyDataSetChanged();
                        }
                    }
                    top_categories.setVisibility(categoryList.size() < 1?View.GONE:View.VISIBLE);
                    all_categories.setVisibility(toplist.size() < 1?View.GONE:View.VISIBLE);
                }else{
                    all_categories.setVisibility(View.GONE);
                    top_categories.setVisibility(View.GONE);
                    loader.setVisibility(View.VISIBLE);
                    reload.setText("No inventory update. Retry?");
                    loading.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                loader.setVisibility(View.GONE);
                refreshLayout.setRefreshing(false);
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
        Intent intent = new Intent(MainActivity.this,Categories.class);
        intent.putExtra(CATEGORY_ID, topItemsLists.get(position).getCategoryid());
        intent.putExtra(CATEGORY_TYPE,"item");
        intent.putExtra(ITEM_ID, topItemsLists.get(position).getId());
        intent.putExtra(ITEM_NAME, topItemsLists.get(position).getName());
        startActivity(intent);

    }

    @Override
    public void onCategoryCardClick(int position) {
        Log.i(TAG, "onCategoryCardClick: "+categoryList.get(position).getCategoryID());
        Intent intent = new Intent(MainActivity.this, Categories.class);
        intent.putExtra(CATEGORY_ID,categoryList.get(position).getCategoryID());
        intent.putExtra(CATEGORY_TYPE, "category");
        startActivity(intent);
    }

    @Override
    public void ExploreShopClick(int position) {
        Intent intent = new Intent(MainActivity.this, Categories.class);
        intent.putExtra(CATEGORY_ID, toplist.get(position).getCategoryID());
        intent.putExtra(CATEGORY_TYPE, "category");
        startActivity(intent);
    }
}
