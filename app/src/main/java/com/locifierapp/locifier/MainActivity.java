package com.locifierapp.locifier;


import android.Manifest;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.locifierapp.locifier.alarm.Alarm;
import com.locifierapp.locifier.notification.ArrivalNotification;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.ui.AppBarConfiguration;
import java.util.List;
import static java.lang.Math.toDegrees;
import static java.lang.Math.toRadians;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    private GoogleMap googleMap;
    private LocationRequest locationRequest;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Marker destinationMarker;
    private Circle destinationAreaCircle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();


        initializeMapFragment();
        configureTestActivityButton();
        configureSettingsActivityButton();
        configureAccountActivityButton();
        Alarm.stop();

    }

    @Override
    public void onPause() {
        super.onPause();

        //stop location updates when Activity is no longer active
        if (fusedLocationProviderClient != null) {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        this.googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);

        //Check SDK Version
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //Check if app has permission to use location assets
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                initializeLocationRequest();
                fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
                this.googleMap.setMyLocationEnabled(true);
            } else {
                //Ask for permission
                requestLocationPermission();
            }
        }
        else {
            initializeLocationRequest();
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
            this.googleMap.setMyLocationEnabled(true);
        }

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                //Add marker functionality
                if(destinationAreaCircle != null && destinationMarker != null){
                    destinationAreaCircle.setVisible(false);
                    destinationMarker.setVisible(false);
                }
                final Marker newDestinationMarker = addDestinationMarkerOnMap(point, MainActivity.this.googleMap);
                final Circle newDestinationArea = addDestinationArea(point, MainActivity.this.googleMap);

                //REFACTOR!
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Confirm destination")
                        .setMessage("Is this where you want to wake up?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if(destinationAreaCircle != null && destinationMarker != null){
                                    removeMarkers();
                                    removeDestinationCircle();
                                }
                                destinationMarker = newDestinationMarker;
                                destinationAreaCircle = newDestinationArea;
                            }
                        })
                        .setNegativeButton(android.R.string.no,  new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                newDestinationMarker.remove();
                                newDestinationArea.remove();
                                if(destinationAreaCircle != null && destinationMarker != null){
                                    destinationAreaCircle.setVisible(true);
                                    destinationMarker.setVisible(true);
                                }
                            }
                        })
                        .setCancelable(false)
                        .show();
            }
        });
    }

    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            List<Location> locationList = locationResult.getLocations();
            if (locationList.size() > 0) {
                //The last location in the list is the newest
                Location location = locationList.get(locationList.size() - 1);
                if(destinationMarker != null && userIsInRadius(new LatLng(location.getLatitude(), location.getLongitude()), new LatLng(destinationMarker.getPosition().latitude, destinationMarker.getPosition().longitude))){
                    sendNotification();
                    Alarm.play(MainActivity.this);
                }
            }
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
                        googleMap.setMyLocationEnabled(true);
                    }
                } else {
                    // permission denied, boo! Disable the functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public void removeMarkers(){
        if(destinationMarker != null){
            destinationMarker.remove();
            destinationMarker = null;
        }
    }

    public void removeDestinationCircle(){
        if(destinationAreaCircle != null){
            destinationAreaCircle.remove();
            destinationAreaCircle = null;
        }
    }

    private void requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION );
        }
    }

    private void configureTestActivityButton(){
        Button sendNotificationButton = (Button) findViewById(R.id.test_activity_button);

        sendNotificationButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, TestActivity.class));
            }
        });
    }

    private void configureSettingsActivityButton(){
        ImageView goToSettingsButton = (ImageView) findViewById(R.id.settings_button);

        goToSettingsButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            }
        });
    }

    private void configureAccountActivityButton(){
        ImageView goToSettingsButton = (ImageView) findViewById(R.id.account_button);

        goToSettingsButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            }
        });
    }

    private void initializeMapFragment(){
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        SupportMapFragment mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
        mapFrag.getMapAsync(this);
    }

    private void initializeLocationRequest(){
        locationRequest = new LocationRequest();
        locationRequest.setInterval(30000); // two minute interval
        locationRequest.setFastestInterval(30000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
    }

    private boolean userIsInRadius(LatLng currentUserCoordinates, LatLng destinationCoordinates){
        if(distance(currentUserCoordinates.latitude, currentUserCoordinates.longitude, destinationCoordinates.latitude, destinationCoordinates.longitude) <= 0.5){
            return true;
        }
        return false;
    }

    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(toRadians(lat1))
                * Math.sin(toRadians(lat2))
                + Math.cos(toRadians(lat1))
                * Math.cos(toRadians(lat2))
                * Math.cos(toRadians(theta));
        dist = Math.acos(dist);
        dist = toDegrees(dist);
        dist = dist * 69 * 1.609344;
        return (dist);}

    private void sendNotification(){
            Intent intent = new Intent(MainActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(MainActivity.this, 0, intent, 0);

            new ArrivalNotification(pendingIntent, MainActivity.this);
        }

    private Marker addDestinationMarkerOnMap(LatLng point, GoogleMap googleMap){
            MarkerOptions marker = new MarkerOptions().position(new LatLng(point.latitude, point.longitude)).title("Destination");
            return googleMap.addMarker(marker);
        }

    private Circle addDestinationArea(LatLng point, GoogleMap googleMap){
          return googleMap.addCircle(new CircleOptions().center(new LatLng(point.latitude, point.longitude)).radius(500).strokeWidth(3f).strokeColor(Color.RED).fillColor(Color.argb(70, 150, 50,50)));
       }

}
