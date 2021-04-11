package com.amsabots.suzzy.GeneralClasses;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.widget.TextView;

import com.amsabots.suzzy.CreateAccount;
import com.amsabots.suzzy.MainFrags.MoreFrag;
import com.amsabots.suzzy.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;

public class General {
    public static String residence = "location";

    public void openAccountCreation(final Context ctx) {
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
    public static String getResidence() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
        String USERID = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference mref = FirebaseDatabase.getInstance().getReference().child("Users")
                    .child(USERID).child("location");
            mref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        residence = dataSnapshot.child("residence").getValue().toString()+", "+
                                dataSnapshot.child("city").getValue().toString();
                    } else residence = "not set";
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    residence = "not set";
                }
            });
        } else residence = "not set";
        return residence;
    }
    public void openEditLocation(Context context){
     context.startActivity(new Intent(context, MoreFrag.class));
    }
    public static void underLineTextview(TextView textview){
textview.setPaintFlags(textview.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
    }
}
