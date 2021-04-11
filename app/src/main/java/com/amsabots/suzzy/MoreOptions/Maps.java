package com.amsabots.suzzy.MoreOptions;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.amsabots.suzzy.GeneralClasses.General;
import com.amsabots.suzzy.MainFrags.MoreFrag;
import com.amsabots.suzzy.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class Maps extends AppCompatActivity implements OnMapReadyCallback {
    private static final String TAG = "Maps";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private boolean locationPermissionGranted = false;
    private static final int MAPS_REQUEST_CODE = 5426;
    private GoogleMap googleMapInt;
    FusedLocationProviderClient fusedLocationProviderClient;
    Location currentLocation;
    private MaterialButton btn;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Log.i(TAG, "onCreate: maps is being initilised");
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        getLocationPermission();
        btn = findViewById(R.id.map_update_btn);
        progressDialog = new ProgressDialog(Maps.this);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if(FirebaseAuth.getInstance().getCurrentUser() != null) updateLocation();
            else new General().openAccountCreation(Maps.this);
            }
        });
        btn.setEnabled(false);
        btn.setText("fetching coordinates");

    }

    private void updateLocation() {
        progressDialog.setMessage("Updating.....");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        Map<String, Object> params = new HashMap<>();
        params.put("lat", currentLocation.getLatitude());
        params.put("longi", currentLocation.getLongitude());
        params.put("provider", currentLocation.getProvider());

        FirebaseDatabase.getInstance().getReference().child("Users")
        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
        .child("location").updateChildren(params).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressDialog.dismiss();
               if(task.isSuccessful()){
                   new MaterialAlertDialogBuilder(Maps.this)
                           .setMessage("Your geo Coordinates have been set successfully")
                           .setIcon(getResources().getDrawable(R.drawable.ic_info_black_24dp))
                           .setPositiveButton("Track package", new DialogInterface.OnClickListener() {
                               @Override
                               public void onClick(DialogInterface dialog, int which) {
                                   startActivity(new Intent(Maps.this, History.class));
                               }
                           }).setNegativeButton("Back", new DialogInterface.OnClickListener() {
                       @Override
                       public void onClick(DialogInterface dialog, int which) {
                           startActivity(new Intent(Maps.this, MoreFrag.class));
                       }
                   }).show();
               } else{
                   Toast.makeText(Maps.this, "Please try again later, Network error occured", Toast.LENGTH_SHORT).show();
               }
            }
        });
    }

    void getLocationPermission() {
        Log.i(TAG, "getLocationPermission: checking permissions");
        String[] permission = {FINE_LOCATION, COURSE_LOCATION};
        if (ContextCompat.checkSelfPermission(Maps.this, COURSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(Maps.this, FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
            initMaps();

        }else{
            ActivityCompat.requestPermissions(Maps.this, permission, MAPS_REQUEST_CODE);
        }

    }
//override request permission callback to check for result from the request above

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionsResult: has been called");
        locationPermissionGranted = false;
        switch (requestCode) {
            case MAPS_REQUEST_CODE:
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            locationPermissionGranted = false;
                            return;
                        }
                    }
                    locationPermissionGranted = true;
                    initMaps();
                }
                break;
        }
    }

    void initMaps() {
        Log.i(TAG, "initMaps: it has been called already");
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location != null){
                    btn.setEnabled(true);
                    btn.setText("Update location coordinates");
                    Log.i(TAG, "onSuccess: "+location.toString());
                    currentLocation = location;
                    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                            .findFragmentById(R.id.map);
                    mapFragment.getMapAsync(Maps.this);
                }

            }
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "Please wait.....", Toast.LENGTH_SHORT).show();
        Log.i(TAG, "onMapReady: map is ready");
        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions().anchor(0.5f, 0.5f)
                .position(latLng).title("Current Position");
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        googleMap.addMarker(markerOptions);
    }
}
