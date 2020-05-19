package com.example.suzzy.MoreOptions;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.suzzy.MainActivity;
import com.example.suzzy.R;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Account extends AppCompatActivity implements View.OnClickListener {
    MaterialToolbar toolbar;
    MaterialButton delete, logout;
    ProgressDialog progressDialog;
    TextView email, time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        toolbar = findViewById(R.id.account_toolbar);
        delete = findViewById(R.id.account_delete_account);
        logout = findViewById(R.id.account_logout);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        progressDialog = new ProgressDialog(this);
        delete.setOnClickListener(this);
        logout.setOnClickListener(this);
        email = findViewById(R.id.account_email);
        time = findViewById(R.id.account_date_stamp);
        FirebaseDatabase.getInstance().getReference().child("Users").child(
                FirebaseAuth.getInstance().getCurrentUser().getUid()
        ).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
              email.setText(dataSnapshot.child("email").getValue().toString());
              time.setText(
                      sfd.format(new Date(dataSnapshot.child("date_created").getValue(Long.class)))
              );
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
                                                if (task.isSuccessful()){

                                                    Toast.makeText(Account.this, "Successfully Logged out", Toast.LENGTH_SHORT).show();
                                                    startActivity(new Intent(Account.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|
                                                            Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                                    finish();
                                                }else{
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
                                FirebaseDatabase.getInstance().getReference().child("Users")
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        progressDialog.dismiss();
                                        if(task.isSuccessful()){
                                            progressDialog.setMessage("Logging you off from the App......");
                                            progressDialog.show();
                                            progressDialog.setCanceledOnTouchOutside(false);
                                            AuthUI.getInstance()
                                                    .delete(Account.this)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            progressDialog.dismiss();
                                                            if (task.isSuccessful()){
                                                                Toast.makeText(Account.this, "Your account has been successfully deleted", Toast.LENGTH_SHORT).show();
                                                                startActivity(new Intent(Account.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|
                                                                        Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                                                finish();

                                                            }else{
                                                                Toast.makeText(Account.this, "Action failed Please try again", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });


                                        }else{
                                            Toast.makeText(Account.this, "Network error, Please try again", Toast.LENGTH_SHORT).show();
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
        }
    }
}
