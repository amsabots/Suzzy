package com.amsabots.suzzy.MoreOptions;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.amsabots.suzzy.BottomSheets.OrderDetails;
import com.amsabots.suzzy.BottomSheets.OrderList;
import com.amsabots.suzzy.Cart.Categories;
import com.amsabots.suzzy.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.amsabots.suzzy.MainActivity.CATEGORY_TYPE;

public class History extends AppCompatActivity implements HistoryAdapter.OnCardItemClickListener,
        OrderDetails.DeleteItemSelected {
    String mUser;
    DatabaseReference mref;
    ProgressDialog progressDialog;
    MaterialToolbar toolbar;
    RecyclerView recyclerView;
    HistoryAdapter adapter;
    List<OrderList> mainList;
    OrderDetails orderDetails;
    public static final String ORDER_ID = "last kings";
    public static final String ORDER_POSITION = "trial and error";
    private static final String TAG = "History";
    SwipeRefreshLayout refresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        mUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Log.i(TAG, "onCreate: " + FirebaseAuth.getInstance().getCurrentUser().getDisplayName() + " " +
                FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString());
        mref = FirebaseDatabase.getInstance().getReference();
        progressDialog = new ProgressDialog(this);
        toolbar = findViewById(R.id.history_toolbar);
        setSupportActionBar(toolbar);
        mainList = new ArrayList<>();
        refresh = findViewById(R.id.history_refresh);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Orders History");
        recyclerView = findViewById(R.id.history_recclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new HistoryAdapter(mainList, History.this);
        adapter.setOnCardItemClickListener(History.this);
        recyclerView.setAdapter(adapter);
        orderDetails = OrderDetails.newInstance(this);
        getHistory();

        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getHistory();
            }
        });
    }


    private void getHistory() {
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Please wait.....");
        progressDialog.show();
        mref.child("Orders").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mainList.clear();
                Log.i(TAG, "onDataChange: " + dataSnapshot.toString());
                progressDialog.dismiss();
                refresh.setRefreshing(false);
                for (DataSnapshot data : dataSnapshot.getChildren()
                ) {
                    if (TextUtils.equals(data.child("Customer").getValue().toString(), mUser)) {
                        OrderList orderList = new OrderList();
                        orderList.setAmount(data.child("amount").getValue(Long.class));
                        orderList.setId(data.getKey());
                        orderList.setTime(data.child("time").getValue(Long.class));
                        orderList.setStatus(data.child("status").getValue().toString());
                        mainList.add(orderList);
                        adapter.notifyDataSetChanged();
                    }

                }
                if (mainList.size() < 1) {
                    new MaterialAlertDialogBuilder(History.this)
                            .setIcon(R.drawable.ic_info_black_24dp)
                            .setCancelable(false)
                            .setTitle("No History")
                            .setMessage("You have no app History at the moment, Continue shopping with us")
                            .setPositiveButton("Start Shopping", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(History.this, Categories.class);
                                    intent.putExtra(CATEGORY_TYPE, "all");
                                    startActivity(intent);
                                }
                            }).show();
                }
                Log.i(TAG, "onDataChange: LISTSIZE " + mainList.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
                refresh.setRefreshing(false);
                Toast.makeText(History.this, "We cannot complete your request at this moment",
                        Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onItemClick(int position) {
        Bundle bundle = new Bundle();
        bundle.putString(ORDER_ID, mainList.get(position).getId());
        bundle.putInt(ORDER_POSITION, position);
        orderDetails.setArguments(bundle);
        orderDetails.show(getSupportFragmentManager(), "order_details");
    }

    @Override
    public void deleteSelectedItem(final int position) {
        final DatabaseReference orders = FirebaseDatabase.getInstance().getReference().child("Orders")
                .child(mainList.get(position).getId());
        progressDialog.setMessage("Deleting.....");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        orders.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                progressDialog.dismiss();
                if (dataSnapshot.child("delete").getValue(Boolean.class)) {
                    orders.removeValue().addOnCompleteListener(
                            new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(History.this, "Item removed successfully", Toast.LENGTH_SHORT).show();
                                        mainList.remove(position);
                                        adapter.notifyItemRemoved(position);
                                        adapter.notifyItemRangeRemoved(position, mainList.size());
                                        orderDetails.dismiss();
                                    }
                                }
                            }
                    );
                } else {
                    new MaterialAlertDialogBuilder(History.this)
                            .setMessage("This Order cannot be removed since its already on transit to your specified location")
                            .setIcon(getResources().getDrawable(R.drawable.ic_warning_black_24dp))
                            .show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}



