package com.example.suzzy.MoreOptions;

import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.example.suzzy.MainFrags.MoreFrag;
import com.example.suzzy.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class Maps extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        new MaterialAlertDialogBuilder(this)
                .setIcon(R.drawable.ic_info_black_24dp)
                .setCancelable(false)
                .setTitle("Google play services Alert")
                .setMessage("Your device is requesting to use Google maps Location from a registered BUT unverified " +
                        "Google Maps and user Activity API. Please exit and contact owner with the Error shown")
                .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(Maps.this, MoreFrag.class));
                    }
                }).show();
    }
}
