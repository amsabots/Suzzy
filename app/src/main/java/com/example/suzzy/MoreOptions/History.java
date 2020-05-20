package com.example.suzzy.MoreOptions;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.suzzy.Cart.Categories;
import com.example.suzzy.MainFrags.CartFrag;
import com.example.suzzy.MainFrags.MoreFrag;
import com.example.suzzy.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class History extends AppCompatActivity {
String mUser;
DatabaseReference mref;
ProgressDialog progressDialog;
MaterialToolbar toolbar;
RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        mUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mref = FirebaseDatabase.getInstance().getReference();
        progressDialog = new ProgressDialog(this);
        toolbar = findViewById(R.id.history_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Orders History");
        recyclerView = findViewById(R.id.history_recclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        getHistory();

    }

    private void getHistory() {
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Please wait.....");
        progressDialog.show();
        mref.child("Orders").orderByChild("Customer").equalTo(mUser).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                progressDialog.dismiss();
                if(!dataSnapshot.exists()){

                }else{
                    new MaterialAlertDialogBuilder(History.this)
                            .setIcon(R.drawable.ic_info_black_24dp)
                            .setCancelable(false)
                            .setTitle("No History")
                            .setMessage("You have no app History at the moment, Continue shopping with us")
                            .setPositiveButton("Start Shopping", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(History.this, Categories.class);
                                    intent.putExtra("type", "all");
                                    startActivity(intent);
                                }
                            }).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
progressDialog.dismiss();
                Toast.makeText(History.this, "Error!!!!, Check your internet connection", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
