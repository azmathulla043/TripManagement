package com.roam.tripmanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    public static final int UPDATE_INTERVAL = 5000;
    private int LOCATION_PERMISSION = 100;

    FusedLocationProviderClient mlocationProviderClient;
    LocationRequest mlocationRequest;
    LocationCallback mlocationCallback;

    private Location mCurrentLoactiom;
    private Button TripStarted, TripEnded;
    TextView longitude, latitude;
    Bundle mBundle ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Initilizing the views
        mBundle = null;

        initViews();
        initLocation();

    }

    private void initViews() {
        TripStarted = findViewById(R.id.startTrip);
        TripEnded = findViewById(R.id.endTrip);
        longitude = findViewById(R.id.longtitude);
        latitude = findViewById(R.id.latitide);
        TripStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StartTripLocation();
            }
        });
        TripEnded.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EndTripLocation();
            }
        });
    }

    private void initLocation() {
        // Initilizing the location Geo loaction
        mlocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        mlocationRequest = LocationRequest.create();
        mlocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mlocationRequest.setInterval(UPDATE_INTERVAL);
        mlocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Log.d(TAG, "onLocationResult is avalible");
            }

            @Override
            public void onLocationAvailability(@NonNull LocationAvailability locationAvailability) {
                super.onLocationAvailability(locationAvailability);
                if (locationAvailability.isLocationAvailable()) {
                    Log.d(TAG, "onLocationAvailability is avalible");
                } else {
                    Log.d(TAG, "onLocationAvailability is not avalible");
                }
            }
        };

    }

    private void StartTripLocation() {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)== PackageManager.PERMISSION_GRANTED){
            mlocationProviderClient.requestLocationUpdates(mlocationRequest,mlocationCallback,this.getMainLooper());
            mlocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    mCurrentLoactiom = location;
                    Log.d(TAG, "onSuccess: "+location);
                    if(location != null){
                        longitude.setText(""+ mCurrentLoactiom.getLongitude());
                        latitude.setText(""+ mCurrentLoactiom.getLatitude());
                    }
                }
            });
            mlocationProviderClient.getLastLocation().addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "Exception while getting the location "+e.getMessage());
                }
            });
        } else {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)){
                Toast.makeText(this,"Required Permission",Toast.LENGTH_LONG).show();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[] { Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        LOCATION_PERMISSION);
            }
        }
    }

    private void EndTripLocation() {
        mlocationProviderClient.removeLocationUpdates(mlocationCallback);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        StartTripLocation();
    }

    @Override
    protected void onStop() {
        super.onStop();
        EndTripLocation();
    }
}
