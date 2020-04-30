package com.example.suzzy.MainFrags;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.suzzy.FragmentAdapter.ExploreandShopAdapter;
import com.example.suzzy.FragmentAdapter.topCategoryAdapter;
import com.example.suzzy.FragmentAdapter.topItemsAdapter;
import com.example.suzzy.FragmentListClasses.ExploreandShopList;
import com.example.suzzy.FragmentListClasses.topCategoryList;
import com.example.suzzy.FragmentListClasses.topItemsList;
import com.example.suzzy.MainActivity;
import com.example.suzzy.R;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFrag extends Fragment {
    private DatabaseReference mDatabase;
    CoordinatorLayout snackbar;
    private ExploreandShopAdapter adapter;
    private topCategoryAdapter categoryAdapter;
    private topItemsAdapter itemsAdapter;
    private List<topItemsList> topItemsLists;
    private List<ExploreandShopList> toplist;
    private List<topCategoryList> categoryList;
    private RecyclerView exploreRecycler, topcategoryRecyclerview, topitemsRecyclerview;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        //Firebase Settings Initialisers and setting management
        mDatabase = FirebaseDatabase.getInstance().getReference();
        //end of firebase settings call

           //view initialisers
        initExploreandShop(view);
        initTopCategory(view);
        initTopItems(view);
        //view initialisation

        //data loading functions and handlers
        LoadExploreandShopData();
        LoadtopItemsList();
        //end of data loading functions
        snackbar = view.findViewById(R.id.fragment_container);

        return view;
    }

    private void initExploreandShop(View view) {
        toplist = new ArrayList<>();
        exploreRecycler = view.findViewById(R.id.top_category_recyclerview);
        exploreRecycler.setHasFixedSize(true);
        exploreRecycler.setNestedScrollingEnabled(false);
        exploreRecycler.setLayoutManager(new GridLayoutManager(getContext(),
                2, GridLayoutManager.HORIZONTAL, false));
        adapter = new ExploreandShopAdapter(getContext(), toplist);
        exploreRecycler.setAdapter(adapter);
    }

    void initTopCategory(View view) {
        categoryList = new ArrayList<>();
        topcategoryRecyclerview = view.findViewById(R.id.explore_recyclerview);
        topcategoryRecyclerview.setNestedScrollingEnabled(false);
        topcategoryRecyclerview.setLayoutManager(new GridLayoutManager(getContext(), 2, GridLayoutManager.VERTICAL, false));
        categoryAdapter = new topCategoryAdapter(getContext(), categoryList);
        topcategoryRecyclerview.setAdapter(categoryAdapter);


    }
    void initTopItems(View view){
        topItemsLists = new ArrayList<>();
     topitemsRecyclerview = view.findViewById(R.id.topitems_recyclerview);
     topitemsRecyclerview.setLayoutManager(new GridLayoutManager(getContext(),
             2, GridLayoutManager.VERTICAL, false));
     topitemsRecyclerview.setNestedScrollingEnabled(false);
     itemsAdapter = new topItemsAdapter(getContext(), topItemsLists);
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
                Snackbar.make(snackbar, databaseError.getMessage(), BaseTransientBottomBar.LENGTH_SHORT).show();
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
                Snackbar.make(snackbar, databaseError.getMessage(), BaseTransientBottomBar.LENGTH_SHORT).show();
            }
        });
    }

}
