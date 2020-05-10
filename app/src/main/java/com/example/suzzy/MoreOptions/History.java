package com.example.suzzy.MoreOptions;

import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.example.suzzy.MainFrags.MoreFrag;
import com.example.suzzy.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class History extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        new MaterialAlertDialogBuilder(this)
                .setIcon(R.drawable.ic_info_black_24dp)
                .setCancelable(false)
                .setTitle("Firebase Reference Key Warning")
                .setMessage("Invalid key has been used to register all inventorie and and records with exception of " +
                        "Firebase Authentication ID. This maybe due to invalid data entries or temporary storage Malfunction. " +
                        "Inform team responsible for development for a fix")
                .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(History.this, MoreFrag.class));
                    }
                }).show();
    }
}
