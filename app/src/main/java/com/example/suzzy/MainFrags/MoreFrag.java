package com.example.suzzy.MainFrags;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.suzzy.CreateAccount;
import com.example.suzzy.GeneralClasses.General;
import com.example.suzzy.MainActivity;
import com.example.suzzy.MoreOptions.Account;
import com.example.suzzy.MoreOptions.History;
import com.example.suzzy.MoreOptions.Maps;
import com.example.suzzy.R;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;


public class MoreFrag extends AppCompatActivity implements View.OnClickListener,
        BottomNavigationView.OnNavigationItemSelectedListener {
    private TextInputLayout city, residence, phonenumber, name;
    Button update_contact, update_location;
    TextView open_maps;
    LinearLayout open_accounts, history;
    String Currentuser;
    DatabaseReference user;
    SpinKitView address_loader, phone_loader;
    BottomNavigationView bottomNavigationView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_more);
        //init views
        initViews();
        //setting the onclick listeners to th view
        update_location.setOnClickListener(this);
        update_contact.setOnClickListener(this);
        open_maps.setOnClickListener(this);
        open_accounts.setOnClickListener(this);
        history.setOnClickListener(this);

        //initialise firebase class member variables
        if(FirebaseAuth.getInstance().getCurrentUser() !=null) {
            Currentuser = FirebaseAuth.getInstance().getCurrentUser().getUid();
            user = FirebaseDatabase.getInstance().getReference().child("Users").child(Currentuser);
            user.keepSynced(true);
            getAccountDetails();
        }
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.getMenu().findItem(R.id.bottom_nav_more).setChecked(true);

    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fragment_more_update_phonenumber:
                String yourname = name.getEditText().getText().toString();
                String number = phonenumber.getEditText().getText().toString();
                if (!TextUtils.isEmpty(number) && !TextUtils
                .isEmpty(yourname) && number.matches("^07\\d{8}$")) {
                    phone_loader.setVisibility(View.VISIBLE);
                    Map<String, Object> params = new HashMap<>();
                    params.put("phone", phonenumber.getEditText().getText().toString());
                    params.put("name", name.getEditText().getText().toString());

                    user.updateChildren(params).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        phone_loader.setVisibility(View.GONE);
                                        new MaterialAlertDialogBuilder(MoreFrag.this)
                                                .setMessage("Your Info updated successfully")
                                                .setIcon(R.drawable.ic_done_black_24dp)
                                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                    }
                                                }).show();
                                    } else {
                                        phone_loader.setVisibility(View.GONE);
                                        Toast.makeText(MoreFrag.this, "Failed! Please try again", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    Toast.makeText(MoreFrag.this, "Fill both fields and make sure the Phonenumber follows the pattern shown", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.fragment_more_update_address:
                if(!TextUtils.isEmpty(city.getEditText().getText().toString()) && !TextUtils.
                        isEmpty(residence.getEditText().getText().toString())){
                    address_loader.setVisibility(View.VISIBLE);
                    Map<String, Object> params = new HashMap<>();
                    params.put("residence", residence.getEditText().getText().toString());
                    params.put("city", city.getEditText().getText().toString());

                    user.child("location").updateChildren(params).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                               address_loader.setVisibility(View.GONE);
                                new MaterialAlertDialogBuilder(MoreFrag.this)
                                        .setMessage("Delivery address has been set up and updated successfully")
                                        .setIcon(R.drawable.ic_done_black_24dp)
                                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        }).show();
                            } else {
                                address_loader.setVisibility(View.GONE);
                                Toast.makeText(MoreFrag.this, "Failed! Please try again", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }else Toast.makeText(MoreFrag.this, "Fill both residence and city boxes before submitting", Toast.LENGTH_SHORT).show();
                break;
            case R.id.fragment_get_device_location:
                startActivity(new Intent(MoreFrag.this, Maps.class));
                break;
            case R.id.fragment_more_account:
                if(FirebaseAuth.getInstance().getCurrentUser() ==null)
                    new General().openAccountCreation(MoreFrag.this);
                else
                    startActivity(new Intent(MoreFrag.this, Account.class));
                break;
            case R.id.fragment_more_history:
                startActivity(new Intent(MoreFrag.this, History.class));
                break;

        }
    }

void getAccountDetails(){
        address_loader.setVisibility(View.VISIBLE);
        phone_loader.setVisibility(View.VISIBLE);
        user.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                address_loader.setVisibility(View.GONE);
                phone_loader.setVisibility(View.GONE);
              if(dataSnapshot.child("phone").exists()){
                 phonenumber.getEditText().setText(dataSnapshot.child("phone").getValue().toString());
              }
                if(dataSnapshot.child("name").exists()){
                    name.getEditText().setText(dataSnapshot.child("name").getValue().toString());
                }
              if(dataSnapshot.child("location").exists()){
                  city.getEditText().setText(dataSnapshot.child("location").child("city").getValue().toString());
                  residence.getEditText().setText(dataSnapshot.child("location").child("residence").getValue().toString());
              }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void initViews() {
        city = findViewById(R.id.fragment_more_current_city);
        residence = findViewById(R.id.fragment_more_current_residence);
        phonenumber = findViewById(R.id.fragment_more_activephone);
        update_contact = findViewById(R.id.fragment_more_update_phonenumber);
        update_location = findViewById(R.id.fragment_more_update_address);
        open_maps = findViewById(R.id.fragment_get_device_location);
        open_accounts = findViewById(R.id.fragment_more_account);
        history = findViewById(R.id.fragment_more_history);
        address_loader = findViewById(R.id.spinkit_address);
        phone_loader = findViewById(R.id.spinkit_contact);
        open_maps.setPaintFlags(open_maps.getPaintFlags()|Paint.UNDERLINE_TEXT_FLAG);
        name = findViewById(R.id.fragment_more_name);


    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.bottom_nav_cart:
                if(MainActivity.isLogged()) startActivity(new Intent(this, CartFrag.class));
                else new General().openAccountCreation(this);
                break;
            case R.id.bottom_nav_home:
                startActivity(new Intent(this,MainActivity.class))  ;
                break;
            case R.id.bottom_nav_more:
                startActivity(new Intent(this, MoreFrag.class))  ;
                break;
        }
        return false;
    }

}
