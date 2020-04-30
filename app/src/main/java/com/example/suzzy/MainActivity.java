package com.example.suzzy;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.view.MenuItem;

import com.example.suzzy.MainFrags.CartFrag;
import com.example.suzzy.MainFrags.CategoryFrag;
import com.example.suzzy.MainFrags.HomeFrag;
import com.example.suzzy.MainFrags.MoreFrag;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    BottomNavigationView bottomNavigationView;
    CoordinatorLayout layout;
    boolean doubleBackToExitPressedOnce = false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Fresco.initialize(MainActivity.this);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFrag()).commit();
        layout = findViewById(R.id.fragment_container);
        if(FirebaseAuth.getInstance().getCurrentUser() == null){
            startActivity(new Intent(MainActivity.this, CreateAccount.class)
                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK));
            finish();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        switch (item.getItemId()) {
            case R.id.bottom_nav_home:
                transaction.replace(R.id.fragment_container, new HomeFrag()).commit();
               // bottomNavigationView.setSelectedItemId(R.id.bottom_nav_home);
                break;
            case R.id.bottom_nav_cart:
                transaction.replace(R.id.fragment_container, new CartFrag()).commit();
               // bottomNavigationView.setSelectedItemId(R.id.bottom_nav_cart);
                break;
            case R.id.bottom_nav_more:
                transaction.replace(R.id.fragment_container, new MoreFrag()).commit();
               // bottomNavigationView.setSelectedItemId(R.id.bottom_nav_more);
                break;
        }


        return true;
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFrag()).commit();
            doubleBackToExitPressedOnce = true;
            Snackbar.make(layout, "Click again to exit", BaseTransientBottomBar.LENGTH_SHORT).show();
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);


    }
}
