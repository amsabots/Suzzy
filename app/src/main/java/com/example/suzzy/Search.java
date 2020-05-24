package com.example.suzzy;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.suzzy.Cart.Categories;
import com.example.suzzy.Cart.ProductList;
import com.example.suzzy.ExtrasAdapter.SearchAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.example.suzzy.MainActivity.CATEGORY_ID;
import static com.example.suzzy.MainActivity.CATEGORY_TYPE;
import static com.example.suzzy.MainActivity.ITEM_ID;
import static com.example.suzzy.MainActivity.ITEM_NAME;

public class Search extends AppCompatActivity implements SearchAdapter.OnSearchItemClickListener {
ConstraintLayout loader;
ImageView back_arrow;
RecyclerView recyclerView;
SearchView searchView;
List<ProductList> mainList;
SearchAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        loader = findViewById(R.id.search_loader_container);
        back_arrow = findViewById(R.id.search_arrow_back);
        recyclerView = findViewById(R.id.search_recyclerview);
        searchView = findViewById(R.id.search_searchview);
        mainList = new ArrayList<>();
        searchView.setFocusable(true);
        loader.setVisibility(View.GONE);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        getAllItems();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(adapter !=  null){
                    adapter.getFilterResults().filter(newText);
                }
                recyclerView.setAdapter(adapter);
                return false;
            }
        });
        back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
           finish();
            }
        });
    }
void getAllItems(){
loader.setVisibility(View.VISIBLE);
    FirebaseDatabase.getInstance().getReference().child("products")
            .addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    loader.setVisibility(View.GONE);
                    mainList.clear();
                    for (DataSnapshot items:dataSnapshot.getChildren()
                         ) {
                        ProductList list = items.getValue(ProductList.class);
                        mainList.add(list);
                        adapter = new SearchAdapter(mainList);
                        adapter.setOnSearchItemClickListener(Search.this);
                        adapter.notifyDataSetChanged();
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                   loader.setVisibility(View.GONE);
                }
            });
}

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(Search.this, Categories.class);
        intent.putExtra(CATEGORY_ID, mainList.get(position).getCategoryid());
        intent.putExtra(CATEGORY_TYPE,"item");
        intent.putExtra(ITEM_ID, mainList.get(position).getId());
        intent.putExtra(ITEM_NAME, mainList.get(position).getName());
        startActivity(intent);
    }

    @Override
    public void attachedFilteredList(List<ProductList> list) {

    }


}
