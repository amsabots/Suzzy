package com.example.suzzy.MainFrags;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.suzzy.CreateAccount;
import com.example.suzzy.MoreOptions.Account;
import com.example.suzzy.MoreOptions.History;
import com.example.suzzy.MoreOptions.Maps;
import com.example.suzzy.R;
import com.google.android.material.textfield.TextInputLayout;


public class MoreFrag extends Fragment implements View.OnClickListener {
    private TextInputLayout city, residence, phonenumber;
    Button update_contact, update_location;
    TextView open_maps;
    LinearLayout open_accounts, history;

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
//setting the onclick listeners to th views
        update_location.setOnClickListener(this);
        update_contact.setOnClickListener(this);
        open_maps.setOnClickListener(this);
        open_accounts.setOnClickListener(this);
        return view;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fragment_more_update_phonenumber:
                break;
            case R.id.fragment_more_update_address:
                break;
            case R.id.fragment_get_device_location:
                startActivity(new Intent(getContext(), Maps.class));
                break;
            case R.id.fragment_more_account:
                startActivity(new Intent(getContext(), Account.class));
                break;
            case R.id.fragment_more_history:
                startActivity(new Intent(getContext(), History.class));
                break;

        }
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
    }


}
