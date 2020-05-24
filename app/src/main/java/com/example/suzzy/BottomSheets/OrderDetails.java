package com.example.suzzy.BottomSheets;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.suzzy.Cart.ProductDetails_Adapter;
import com.example.suzzy.Cart.ProductList;
import com.example.suzzy.FragmentListClasses.topItemsList;
import com.example.suzzy.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.example.suzzy.MoreOptions.History.ORDER_ID;
import static com.example.suzzy.MoreOptions.History.ORDER_POSITION;

public class OrderDetails extends BottomSheetDialogFragment {
    static Context mcontext;

    public static OrderDetails newInstance(Context context) {
        mcontext = context;
        return new OrderDetails();
    }

    TextView rider_name, rider_phone, rider_reg, message, status, packaging, payment_method;
    ConstraintLayout rider_details;
    RecyclerView items;
    MaterialButton delete;
    ImageView done_ticks;
    private static final String TAG = "OrderDetails";
    String orderID = null;
    ProgressDialog progressDialog;
    List<topItemsList> mainList;
    ProductDetails_Adapter adapter;
    LinearLayout rider_overlay;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.order_details_bottom_sheet, container, false);
        initViews(view);
        progressDialog = new ProgressDialog(getContext());
        orderID = getArguments().getString(ORDER_ID);
        getOrderInfo(orderID);
        mainList = new ArrayList<>();
        adapter = new ProductDetails_Adapter(getContext(), mainList);
        items.setHasFixedSize(true);
        items.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL,
                false));
        items.setAdapter(adapter);
        Log.d(TAG, "onCreateView: ORDERID " + orderID);

        return view;
    }

    private void getItems(String orderID) {
        mainList.clear();
    FirebaseDatabase.getInstance().getReference().child("Orders").child(orderID)
    .addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            for (DataSnapshot goods:dataSnapshot.child("items").getChildren()
                 ) {
                FirebaseDatabase.getInstance().getReference().child("products")
                        .child(goods.child("id").getValue().toString()).addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                progressDialog.dismiss();
                              topItemsList list = dataSnapshot.getValue(topItemsList.class);
                              mainList.add(list);
                              adapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                progressDialog.dismiss();
                                Toast.makeText(getContext(), "Network Error", Toast.LENGTH_SHORT).show();
                            }
                        }
                );

            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    });
    }

    private void getOrderInfo(String string) {
        progressDialog.setMessage("Please wait.......");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        FirebaseDatabase.getInstance().getReference()
                .child("Orders")
                .child(string).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                getItems(orderID);
                payment_method.setText(dataSnapshot.child("payment_method").getValue().toString());
                done_ticks.setVisibility(!dataSnapshot.child("status").getValue().toString()
                        .equalsIgnoreCase("Pending confirmation")?View.VISIBLE:View.GONE);
                done_ticks.setImageDrawable(dataSnapshot.child("status").getValue().toString()
                        .equalsIgnoreCase("Order delivered")?
                        getResources().getDrawable(R.drawable.ic_done_all_black_24dp):
                        getResources().getDrawable(R.drawable.ic_done_black_24dp));
                message.setText(getDatasnapshotValue("message", dataSnapshot));
                status.setText(getDatasnapshotValue("status", dataSnapshot));
                if (dataSnapshot.child("rider").exists()) {
                    rider_name.setText(getDatasnapshotValue("rider/name", dataSnapshot));
                    rider_phone.setText(getDatasnapshotValue("rider/phone", dataSnapshot));
                    rider_reg.setText(getDatasnapshotValue("rider/reg", dataSnapshot));
                }
                rider_details.setVisibility(dataSnapshot.child("rider").exists()?View.VISIBLE:View.INVISIBLE);
                rider_overlay.setVisibility(dataSnapshot.child("rider").exists()?View.GONE:View.VISIBLE);
                packaging.setText(dataSnapshot.child("packaging").getValue(Boolean.class)?"Packaging is Done":
                        "Please await Packaging Status");
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Error occured, Please check yor internet connection",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initViews(View view) {
        rider_name = view.findViewById(R.id.rider_name);
        rider_phone = view.findViewById(R.id.rider_phone_number);
        rider_reg = view.findViewById(R.id.rider_reg);
        message = view.findViewById(R.id.packaging_message);
        status = view.findViewById(R.id.packaging_status);
        packaging = view.findViewById(R.id.packaging_order);
        done_ticks = view.findViewById(R.id.packaging_done);
        rider_details = view.findViewById(R.id.rider_detail_info);
        items = view.findViewById(R.id.packaging_recyclerview);
        delete = view.findViewById(R.id.packaging_delete_btn);
        payment_method = view.findViewById(R.id.payment_method);
        rider_overlay = view.findViewById(R.id.rider_overlay);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialAlertDialogBuilder(getContext())
                        .setMessage("are you sure you want to proceed with the action")
                        .setTitle("Delete order")
                        .setIcon(getResources().getDrawable(R.drawable.ic_warning_black_24dp))
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                itemSelected.deleteSelectedItem(getArguments().getInt(ORDER_POSITION));
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();

            }
        });
    }

    String getDatasnapshotValue(String childname, DataSnapshot snapshot) {
        return snapshot.child(childname).getValue().toString();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
         itemSelected = (DeleteItemSelected)context;
        }catch (ClassCastException e){
            throw  new ClassCastException(e+"Must implement the interface");
        }
    }
DeleteItemSelected itemSelected;
    public interface DeleteItemSelected{
        void deleteSelectedItem(int position);
    }

}
