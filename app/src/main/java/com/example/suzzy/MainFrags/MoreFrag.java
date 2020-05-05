package com.example.suzzy.MainFrags;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.suzzy.CreateAccount;
import com.example.suzzy.GeneralClasses.General;
import com.example.suzzy.MoreOptions.Account;
import com.example.suzzy.MoreOptions.History;
import com.example.suzzy.MoreOptions.Maps;
import com.example.suzzy.R;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;


public class MoreFrag extends Fragment implements View.OnClickListener {
    private TextInputLayout city, residence, phonenumber;
    Button update_contact, update_location;
    TextView open_maps;
    LinearLayout open_accounts, history;
    String Currentuser;
    DatabaseReference user;
    SpinKitView address_loader, phone_loader;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_more, container, false);
        //initialise all the views on the xml
        initViews(view);
//setting the onclick listeners to th view
        update_location.setOnClickListener(this);
        update_contact.setOnClickListener(this);
        open_maps.setOnClickListener(this);
        open_accounts.setOnClickListener(this);
        //initialise firebase class member variables


        if(FirebaseAuth.getInstance().getCurrentUser() !=null) {
            Currentuser = FirebaseAuth.getInstance().getCurrentUser().getUid();
            user = FirebaseDatabase.getInstance().getReference().child("Users").child(Currentuser);
            user.keepSynced(true);
            getAccountDetails();
        }
        return view;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fragment_more_update_phonenumber:
                if (!TextUtils.isEmpty(phonenumber.getEditText().getText().toString())) {
                    phone_loader.setVisibility(View.VISIBLE);
                    user.child("phone").setValue(phonenumber.getEditText().getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        phone_loader.setVisibility(View.GONE);
                                        new MaterialAlertDialogBuilder(getContext())
                                                .setMessage("Phone Number updated successfully")
                                                .setIcon(R.drawable.ic_done_black_24dp)
                                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                    }
                                                }).show();
                                    } else {
                                        phone_loader.setVisibility(View.GONE);
                                        Toast.makeText(getContext(), "Failed! Please try again", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    Toast.makeText(getContext(), "Fill the phone number field", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.fragment_more_update_address:
                if(!TextUtils.isEmpty(city.getEditText().getText().toString()) && !TextUtils.
                        isEmpty(residence.getEditText().getText().toString())){
                    address_loader.setVisibility(View.VISIBLE);
                    HashMap<String, String> params = new HashMap<>();
                    params.put("residence", residence.getEditText().getText().toString());
                    params.put("city", city.getEditText().getText().toString());

                    user.child("location").setValue(params).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                               address_loader.setVisibility(View.GONE);
                                new MaterialAlertDialogBuilder(getContext())
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
                                Toast.makeText(getContext(), "Failed! Please try again", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }else Toast.makeText(getContext(), "Fill both residence and city boxes before submitting", Toast.LENGTH_SHORT).show();
                break;
            case R.id.fragment_get_device_location:
                startActivity(new Intent(getContext(), Maps.class));
                break;
            case R.id.fragment_more_account:
                if(FirebaseAuth.getInstance().getCurrentUser() ==null)
                    new General().openAccountCreation(getContext());
                else
                    startActivity(new Intent(getContext(), Account.class));
                break;
            case R.id.fragment_more_history:
                startActivity(new Intent(getContext(), History.class));
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
    private void initViews(View view) {
        city = view.findViewById(R.id.fragment_more_current_city);
        residence = view.findViewById(R.id.fragment_more_current_residence);
        phonenumber = view.findViewById(R.id.fragment_more_activephone);
        update_contact = view.findViewById(R.id.fragment_more_update_phonenumber);
        update_location = view.findViewById(R.id.fragment_more_update_address);
        open_maps = view.findViewById(R.id.fragment_get_device_location);
        open_accounts = view.findViewById(R.id.fragment_more_account);
        history = view.findViewById(R.id.fragment_more_history);
        address_loader = view.findViewById(R.id.spinkit_address);
        phone_loader = view.findViewById(R.id.spinkit_contact);

    }


}
