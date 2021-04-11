package com.amsabots.suzzy.MainFrags;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amsabots.suzzy.Cart.CartList;
import com.amsabots.suzzy.Cart.Categories;
import com.amsabots.suzzy.GeneralClasses.General;
import com.amsabots.suzzy.MainActivity;
import com.amsabots.suzzy.MoreOptions.History;
import com.amsabots.suzzy.BottomSheets.Payment;
import com.amsabots.suzzy.R;
import com.facebook.drawee.view.SimpleDraweeView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.amsabots.suzzy.MainActivity.CATEGORY_TYPE;

public class CartFrag extends AppCompatActivity implements  View.OnClickListener, Payment.SendDataBackToHostingActivity{
    private RecyclerView recyclerView;
    private ProgressDialog progressDialog;
    FirebaseRecyclerAdapter adapter;
    String User, dataListener;
    List<CartList> list;
    TextView subTotal, location, change_location;
    Button checkout;
    Payment payment;
    BottomNavigationView bottomNavigationView;
    ImageView arro_back, cancel_back;
    private static final String TAG = "CartFrag";
    public static final String SUB_TOTALCOST = "nothing in my head right now";
    public static final String TOTAL_ITEMS = "can be many, can be none but who cares";
    long items = 0, subtotal;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_cart);
        // Inflate the layout for this fragment
        //main cart recyclerview
        recyclerView = findViewById(R.id.cart_item_list_recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        subTotal = findViewById(R.id.proceed_to_checkout_sub_total);
        checkout = findViewById(R.id.proceed_to_checkout_button);
        arro_back = findViewById(R.id.cart_get_to_parent);
        cancel_back = findViewById(R.id.cart_get_to_parent_cancel);
        arro_back.setOnClickListener(this);
        cancel_back.setOnClickListener(this);
        checkout.setOnClickListener(this);
        payment = Payment.newInstace();
        User = FirebaseAuth.getInstance()
                .getCurrentUser().getUid();
        getSubtotal(User);
        location = findViewById(R.id.cart_location);
        change_location = findViewById(R.id.cart_change_location);
        setLocation();
       FirebaseDatabase.getInstance().getReference().child("Users").child(User).child("Cart")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if(dataSnapshot.getChildrenCount() < 1){
                            if(dataListener == null){
                                new MaterialAlertDialogBuilder(CartFrag.this)
                                        .setTitle("Start Shopping")
                                        .setMessage("You have nothing in your Cart")
                                        .setIcon(R.drawable.ic_info_black_24dp)
                                        .setPositiveButton("Start shopping", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                startActivity(new Intent(CartFrag.this, MainActivity.class));
                                            }
                                        }).setCancelable(false).show();
                            }

                        }else{
                          items = dataSnapshot.getChildrenCount();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
void setLocation(){
    if (FirebaseAuth.getInstance().getCurrentUser() != null) {
        String USERID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference mref = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(USERID).child("location");
        mref.addListenerForSingleValueEvent(new ValueEventListener() {
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
            if(FirebaseAuth.getInstance().getCurrentUser() == null) new General().openEditLocation(CartFrag.this);
            else new General().openEditLocation(CartFrag.this);
        }
    });
}
    @Override
    public void onClick(View v) {
    switch (v.getId()){
        case R.id.cart_get_to_parent_cancel:
            Intent intent = new Intent(CartFrag.this, Categories.class);
            intent.putExtra(CATEGORY_TYPE, "all");
            startActivity(intent);
            break;
        case R.id.cart_get_to_parent:
            startActivity(new Intent(CartFrag.this, MainActivity.class));
            break;
        case R.id.proceed_to_checkout_button:
            FirebaseDatabase.getInstance().getReference().child("Users").child(User)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                         if(dataSnapshot.exists()){
                             if(!dataSnapshot.child("name").exists()|| !dataSnapshot.child("phone").exists()
                             ||!dataSnapshot.child("location").exists()){
                                new MaterialAlertDialogBuilder(CartFrag.this)
                                        .setMessage("You must provide your valid name, phonenumber and pick up location " +
                                                "before checking out")
                                        .setIcon(getResources().getDrawable(R.drawable.ic_info_black_24dp))
                                        .setPositiveButton("Update Information", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                startActivity(new Intent(CartFrag.this, MoreFrag.class));
                                            }
                                        }).setCancelable(false)
                                        .show();
                             }else{
                                 Bundle bundle = new Bundle();
                                 bundle.putString(SUB_TOTALCOST, subTotal.getTag().toString());
                                 bundle.putLong(TOTAL_ITEMS, items);
                                 payment.setArguments(bundle);
                                 payment.show(getSupportFragmentManager(), "Payment");
                             }
                         }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

            break;

    }
    }

    @Override
    public void dataAttached(String data) {
        dataListener = data;
        payment.dismiss();
        new MaterialAlertDialogBuilder(CartFrag.this)
                .setMessage("Your order has been submitted successfully, Proceed to " +
                        "activity history to track the status of your package")
                .setIcon(R.drawable.ic_info_black_24dp)
                .setPositiveButton("Track Package", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(CartFrag.this, History.class));

                    }
                }).setNegativeButton("Continue Shopping", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(CartFrag.this, Categories.class);
                intent.putExtra(CATEGORY_TYPE, "all");
                startActivity(intent);
            }
        })
                .setCancelable(false)
                .show();
        FirebaseDatabase.getInstance().getReference().child("Users").child(User)
                .child("Cart")
                .removeValue();
        checkout.setEnabled(false);
        checkout.setText("No shopping Items");
    }


    public static class viewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        SimpleDraweeView itemImage;
        TextView name, number, price, tag, unit, size, remove, item_total;
        ImageView add, minus;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.product_name);
            number = itemView.findViewById(R.id.cart_item_text);
            price = itemView.findViewById(R.id.product_price);
            tag = itemView.findViewById(R.id.product_tag);
            unit = itemView.findViewById(R.id.product_unit);
            size = itemView.findViewById(R.id.product_size);
            remove = itemView.findViewById(R.id.product_remove_from_cart);
            add = itemView.findViewById(R.id.cart_item_add);
            minus = itemView.findViewById(R.id.cart_item_minus);
            itemImage = itemView.findViewById(R.id.product_image);
            item_total = itemView.findViewById(R.id.item_total);
            itemView.setOnClickListener(this);
            minus.setOnClickListener(this);
            add.setOnClickListener(this);
            remove.setOnClickListener(this);

        }

        viewHolder.OnItemClickListener listener;

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.cart_item_minus:
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onRemoveItems(position);
                        }
                    }
                    break;
                case R.id.cart_item_add:
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.OnAddItems(position);
                        }
                    }
                    break;
                case R.id.product_remove_from_cart:
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onDeleteItems(position);
                        }
                    }
                    break;

            }
        }

        public interface OnItemClickListener {
            void OnAddItems(int index);

            void onRemoveItems(int index);

            void onDeleteItems(int index);

            void onOpenDetailsActivity(int index);
        }

        public void setOnItemClickListener(OnItemClickListener listener) {
            this.listener = listener;
        }
    }


    @Override
    public void onStart() {
        list = new ArrayList<>();
        super.onStart();
        final Query query = FirebaseDatabase.getInstance().getReference().child("Users").child(User).child("Cart");
        query.keepSynced(true);
        //create the adapter
        FirebaseRecyclerOptions<CartList> options = new FirebaseRecyclerOptions.Builder<CartList>()
                .setQuery(query, new SnapshotParser<CartList>() {
                    @NonNull
                    @Override
                    public CartList parseSnapshot(@NonNull DataSnapshot snapshot) {
                      return snapshot.getValue(CartList.class);
                    }
                }).build();
        adapter = new FirebaseRecyclerAdapter<CartList, viewHolder>(options) {

            @NonNull
            @Override
            public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.cart_item_list_cardview, parent, false);
                viewHolder vh = new viewHolder(view);
                loadviewholder(vh);
                return vh;
            }

            @Override
            protected void onBindViewHolder(@NonNull viewHolder viewHolder,
                                            int position, @NonNull CartList model) {
                list.add(model);
                viewHolder.itemImage.setImageURI(model.getImageurl());
                viewHolder.name.setText(model.getName());
                viewHolder.price.setText("Kshs " + Long.toString(model.getPrice()) + "/");
                viewHolder.size.setText(model.getSize());
                viewHolder.tag.setVisibility(model.getTag() != null ? View.VISIBLE : View.GONE);
                viewHolder.size.setVisibility(model.getSize() != null ? View.VISIBLE : View.GONE);
                viewHolder.tag.setText(model.getTag());
                viewHolder.unit.setText(model.getUnit());
                viewHolder.minus.setEnabled(model.getNumber() > 1 ? true : false);
                viewHolder.number.setText(Long.toString(model.getNumber()));
                viewHolder.item_total.setText("KSh " + (int) (model.getNumber() * model.getPrice()));


            }

        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }


    void loadviewholder(viewHolder vh) {
        final DatabaseReference qry = FirebaseDatabase.getInstance()
                .getReference().child("Users").child(User).child("Cart");
        qry.keepSynced(true);
        vh.setOnItemClickListener(new viewHolder.OnItemClickListener() {
            @Override
            public void OnAddItems(int index) {
                CartList addlist = list.get(index);
                qry.orderByChild("id").equalTo(addlist.getId())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    for (DataSnapshot data : dataSnapshot.getChildren()
                                    ) {
                                        String key = data.getKey();
                                        long item = data.child("number").getValue(Long.class);
                                        qry.child(key).child("number").setValue(item + 1).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    adapter.notifyDataSetChanged();
                                                    //Toast.makeText(CartFrag.this, "+1", Toast.LENGTH_SHORT).show();

                                                } else {
                                                    Toast.makeText(CartFrag.this, "failed, try again", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });


                                    }
                                }else{
                                       new MaterialAlertDialogBuilder(CartFrag.this)
                                               .setTitle("Start Shopping")
                                               .setMessage("You Cart is empty!!!")
                                               .setIcon(R.drawable.ic_info_black_24dp)
                                               .setPositiveButton("Start Shopping", new DialogInterface.OnClickListener() {
                                                   @Override
                                                   public void onClick(DialogInterface dialog, int which) {
                                                       startActivity(new Intent(CartFrag.this, MainActivity.class));
                                                   }
                                               }).setCancelable(false).show();

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(CartFrag.this, "failed, try again", Toast.LENGTH_SHORT).show();
                            }
                        });

            }

            @Override
            public void onRemoveItems(int index) {
                CartList addlist = list.get(index);
                qry.orderByChild("id").equalTo(addlist.getId())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    for (DataSnapshot data : dataSnapshot.getChildren()
                                    ) {
                                        String key = data.getKey();
                                        long item = data.child("number").getValue(Long.class);
                                        qry.child(key).child("number").setValue(item - 1).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    adapter.notifyDataSetChanged();
                                                    //Toast.makeText(CartFrag.this, "-1", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(CartFrag.this, "failed, try again", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });


                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(CartFrag.this, "failed, try again", Toast.LENGTH_SHORT).show();
                            }
                        });
            }

            @Override
            public void onDeleteItems(final int index) {
                CartList addlist = list.get(index);
                qry.orderByChild("id").equalTo(addlist.getId())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    for (DataSnapshot data : dataSnapshot.getChildren()
                                    ) {
                                        String key = data.getKey();
                                        qry.child(key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                       list.remove(index);
                                                       adapter.notifyItemRemoved(index);
                                                       adapter.notifyItemRangeChanged(index, list.size());
                                                } else {
                                                    Toast.makeText(CartFrag.this, "failed, try again", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(CartFrag.this, "failed, try again", Toast.LENGTH_SHORT).show();
                            }
                        });
            }

            @Override
            public void onOpenDetailsActivity(int index) {

            }
        });
    }


    public void getSubtotal(String currentUser) {
        final DatabaseReference qry = FirebaseDatabase.getInstance()
                .getReference().child("Users").child(currentUser).child("Cart");
        qry.keepSynced(true);
        qry.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long total = 0;
                if (dataSnapshot.exists()) {
                    for (DataSnapshot items : dataSnapshot.getChildren()
                    ) {
                        long price = items.child("price").getValue(Long.class);
                        long number = items.child("number").getValue(Long.class);
                        total += (price * number);
                        subTotal.setText("Sub Total: Ksh " + total);
                        subTotal.setTag(total);
                    }
                    if(total ==0){
                        checkout.setVisibility(View.GONE);
                    }
                } else subTotal.setText("Sub Total: Ksh 0");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}



