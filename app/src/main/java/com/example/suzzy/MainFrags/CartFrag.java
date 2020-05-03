package com.example.suzzy.MainFrags;

import android.app.ProgressDialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.suzzy.Cart.CartList;
import com.example.suzzy.Cart.ProductList;
import com.example.suzzy.R;
import com.facebook.drawee.view.SimpleDraweeView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class CartFrag extends Fragment {
    private RecyclerView recyclerView;
    private ProgressDialog progressDialog;
    FirebaseRecyclerAdapter adapter;
    String User;
    List<CartList> list;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cart, container, false);
        //main cart recyclerview
        recyclerView = view.findViewById(R.id.cart_item_list_recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        User = FirebaseAuth.getInstance()
                .getCurrentUser().getUid();
        return view;
    }


    public static class viewHolder extends RecyclerView.ViewHolder {
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
            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.OnAddItems(getAdapterPosition());
                }
            });
            minus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onRemoveItems(getAdapterPosition());
                }
            });
            remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onDeleteItems(getAdapterPosition());
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onOpenDetailsActivity(getAdapterPosition());
                }
            });
        }

        viewHolder.OnItemClickListener listener;

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
                .setQuery(query, CartList.class).build();
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
                viewHolder.minus.setEnabled(model.getNumber()>1?true:false);
                viewHolder.number.setText(Long.toString(model.getNumber()));
                viewHolder.item_total.setText("KSh "+(int) (model.getNumber() * model.getPrice()));



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
                                                    //Toast.makeText(getContext(), "+1", Toast.LENGTH_SHORT).show();

                                                } else {
                                                    Toast.makeText(getContext(), "failed, try again", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });


                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(getContext(), "failed, try again", Toast.LENGTH_SHORT).show();
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
                                                    //Toast.makeText(getContext(), "-1", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(getContext(), "failed, try again", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });


                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(getContext(), "failed, try again", Toast.LENGTH_SHORT).show();
                            }
                        });
            }

            @Override
            public void onDeleteItems(int index) {
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
                                                    Toast.makeText(getContext(), "product has been removed", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(getContext(), "failed, try again", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(getContext(), "failed, try again", Toast.LENGTH_SHORT).show();
                            }
                        });
            }

            @Override
            public void onOpenDetailsActivity(int index) {

            }
        });
    }
}



