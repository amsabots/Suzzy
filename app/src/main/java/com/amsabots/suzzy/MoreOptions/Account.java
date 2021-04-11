package com.amsabots.suzzy.MoreOptions;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.amsabots.suzzy.GeneralClasses.TimeAgo;
import com.amsabots.suzzy.MainActivity;
import com.amsabots.suzzy.MainFrags.MoreFrag;
import com.amsabots.suzzy.R;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Account extends AppCompatActivity implements View.OnClickListener {
    MaterialToolbar toolbar;
    MaterialButton delete, logout, reset;
    ProgressDialog progressDialog;
    TextView email, time;
    DatabaseReference mref;
    String mUser;
    private static final String TAG = "Account";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        mref = FirebaseDatabase.getInstance().getReference();
        mUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        toolbar = findViewById(R.id.account_toolbar);
        delete = findViewById(R.id.account_delete_account);
        logout = findViewById(R.id.account_logout);
        reset = findViewById(R.id.account_reset);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        progressDialog = new ProgressDialog(this);
        delete.setOnClickListener(this);
        logout.setOnClickListener(this);
        reset.setOnClickListener(this);
        email = findViewById(R.id.account_email);
        time = findViewById(R.id.account_date_stamp);
        FirebaseDatabase.getInstance().getReference().child("Users").child(
                FirebaseAuth.getInstance().getCurrentUser().getUid()
        ).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                email.setText(dataSnapshot.child("email").hasChild("email")? dataSnapshot
                        .child("email").getValue().toString():"Not set");
                time.setText(dataSnapshot.child("email").hasChild("time")?
                        TimeAgo.getTimeAgo(dataSnapshot.child("date_created").getValue(Long.class),
                        Account.this):"not set");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.account_logout:
                new MaterialAlertDialogBuilder(Account.this)
                        .setMessage("Please confirm you want to Log Out")
                        .setIcon(R.drawable.ic_info_black_24dp)
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                progressDialog.setMessage("Please wait.....");
                                progressDialog.show();
                                progressDialog.setCanceledOnTouchOutside(false);
                                AuthUI.getInstance()
                                        .signOut(Account.this)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            public void onComplete(@NonNull Task<Void> task) {
                                                progressDialog.dismiss();
                                                if (task.isSuccessful()) {

                                                    Toast.makeText(Account.this, "Successfully Logged out", Toast.LENGTH_SHORT).show();
                                                    startActivity(new Intent(Account.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                                                            Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                                    finish();
                                                } else {
                                                    Toast.makeText(Account.this, "Action failed Please try again", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
                break;
            case R.id.account_delete_account:
                new MaterialAlertDialogBuilder(Account.this)
                        .setMessage("You are about to delete your account details and record activities." +
                                "\nDo you wish to proceed?")
                        .setIcon(R.drawable.ic_warning_black_24dp)
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                progressDialog.setMessage("Deleting User information.....");
                                progressDialog.show();
                                progressDialog.setCanceledOnTouchOutside(false);
                                progressDialog.setMessage("Logging you off from the App......");
                                progressDialog.show();
                                progressDialog.setCanceledOnTouchOutside(false);
                               mref.child("Orders").orderByChild("Customer").equalTo(mUser)
                                       .addListenerForSingleValueEvent(new ValueEventListener() {
                                           @Override
                                           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                               Log.i(TAG, "onDataChange: "+dataSnapshot.toString());
                                               if (dataSnapshot.exists()){
                                                   for (DataSnapshot data:dataSnapshot.getChildren()
                                                        ) {
                                                       mref.child("Orders").child(data.getKey()).removeValue();
                                                   }
                                                   deleteUser();
                                               }else{
                                                   deleteUser();
                                               }
                                           }

                                           @Override
                                           public void onCancelled(@NonNull DatabaseError databaseError) {

                                           }
                                       });

                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
                break;
            case R.id.account_reset:
                new MaterialAlertDialogBuilder(Account.this)
                        .setMessage("Reset account information i.e name, location and phonenumber")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                progressDialog.setMessage("Reseting.....");
                                progressDialog.setCanceledOnTouchOutside(false);
                                progressDialog.show();
                                mref.child("Users").child(mUser).child("name").removeValue();
                                mref.child("Users").child(mUser).child("phone").removeValue();
                                mref.child("Users").child(mUser).child("location").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        progressDialog.dismiss();
                                        startActivity(new Intent(Account.this, MoreFrag.class));
                                    }
                                });
                            }
                        }).setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
                break;
        }
    }

    private void deleteUser() {
        mref.child("Users").child(mUser).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
             if (task.isSuccessful()){
                 AuthUI.getInstance().delete(Account.this).addOnCompleteListener(new OnCompleteListener<Void>() {
                     @Override
                     public void onComplete(@NonNull Task<Void> task) {
                         progressDialog.dismiss();
                         if(task.isSuccessful()){
                             Toast.makeText(Account.this, "Account delete successfully", Toast.LENGTH_SHORT).show();
                             startActivity(new Intent(Account.this, MainActivity.class)
                                     .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK));
                         }
                     }
                 });

             }else{
                 AuthUI.getInstance().delete(Account.this).addOnCompleteListener(new OnCompleteListener<Void>() {
                     @Override
                     public void onComplete(@NonNull Task<Void> task) {
                         progressDialog.dismiss();
                         if(task.isSuccessful()){
                             Toast.makeText(Account.this, "Account delete successfully", Toast.LENGTH_SHORT).show();
                             startActivity(new Intent(Account.this, MainActivity.class)
                                     .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK));
                         }
                     }
                 });
             }
            }
        });
    }
}
