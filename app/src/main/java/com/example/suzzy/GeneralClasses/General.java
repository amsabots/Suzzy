package com.example.suzzy.GeneralClasses;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import com.example.suzzy.CreateAccount;
import com.example.suzzy.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class General {
    public void openAccountCreation(final Context ctx){
        new MaterialAlertDialogBuilder(ctx).
                setMessage("You are not signed in.")
                .setIcon(R.drawable.ic_info_black_24dp)
                .setPositiveButton("Sign in", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ctx.startActivity(new Intent(ctx, CreateAccount.class));
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }
}
