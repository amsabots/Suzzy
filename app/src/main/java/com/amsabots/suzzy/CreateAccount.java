package com.amsabots.suzzy;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;

public class CreateAccount extends AppCompatActivity {
    private static final int RC_SIGN_IN = 10000;
    List<AuthUI.IdpConfig> providers = Arrays.asList(
            new AuthUI.IdpConfig.EmailBuilder().build(),
            new AuthUI.IdpConfig.GoogleBuilder().build(),
            new AuthUI.IdpConfig.FacebookBuilder().build());
    private static final String TAG = "CreateAccount";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setLogo(R.mipmap.background)
                        .build(),
                RC_SIGN_IN);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "onActivityResult: "+TokenClassPreferences.getInstance(getApplicationContext()).getTokenId());
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK) {
                // Successfully signed in
                DatabaseReference Users = FirebaseDatabase.getInstance().getReference().child("Users");
                String user = FirebaseAuth.getInstance().getCurrentUser().getUid();
                if(response.isNewUser()){
                    Map<String, Object> params= new HashMap<>();
                    params.put("email", response.getEmail());
                    params.put("date_created", ServerValue.TIMESTAMP);
                    params.put("fcm", TokenClassPreferences.getInstance(getApplicationContext()).getTokenId());
                    Users.child(user).updateChildren(params);
                    startActivity(new Intent(CreateAccount.this, MainActivity.class)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK));
                    finish();


                }else {
                    Map<String, Object> params= new HashMap<>();
                    params.put("fcm", TokenClassPreferences.getInstance(getApplicationContext()).getTokenId());
                    Users.child(user).updateChildren(params);
                    startActivity(new Intent(CreateAccount.this, MainActivity.class)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK));
                    finish();
                }
                // ...
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
            }
        }
    }

}
