package com.example.damproject;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.damproject.util.UTIL;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private final static int PERMISSIONS_REQUEST_ACCES_FINE_LOCATION = 33;

    private GoogleMap mMap;
    private List<Pin> pins = new ArrayList<>();
    private FusedLocationProviderClient fusedLocationProviderClient;

    private LatLng myLocation;

    // UI Components
    private Button btn;

    private class Pin {
        private final static int WIDTH = 90;
        private final static int HEIGHT = 90;

        private String title;
        private LatLng coords;
        private Bitmap icon;
        private String snippet;

        public Pin(String title, double lat, double lng, Bitmap icon, String snippet) {
            this.title = title;
            this.coords = new LatLng(lat, lng);
            this.icon = Bitmap.createScaledBitmap(icon, WIDTH, HEIGHT, false);
            this.snippet = snippet;
        }

        public String getSnippet() {
            return snippet;
        }

        public LatLng getCoords() {
            return coords;
        }

        public String getTitle() {
            return title;
        }

        public Bitmap getIcon() {
            return icon;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        btn = findViewById(R.id.maps_btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myLocation != null) {
                    moveToClosestCenter();
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.maps_location_error), Toast.LENGTH_LONG).show();
                }
            }
        });


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Setup location
        setupLocation();

        // Setup pins
        setupPins();
        for (Pin pin : pins) {
            mMap.addMarker(
                    new MarkerOptions()
                            .position(pin.getCoords())
                            .title(pin.getTitle())
                            .icon(BitmapDescriptorFactory.fromBitmap(pin.getIcon()))
                            .snippet(pin.getSnippet())
            );
        }

        // Setup camera
        setupCamera();
    }

    // Default camera on the first pin
    private void setupCamera() {
        CameraPosition position = CameraPosition.builder()
                .target(pins.get(0).getCoords())
                .zoom(15)
                .tilt(30)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(position));
    }

    private void setupLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
            activateMyLocation();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCES_FINE_LOCATION);
        }
    }

    private void activateMyLocation() {
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        setCurrentLocation();
    }

    private void deActivateMyLocation() {
        mMap.setMyLocationEnabled(false);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
    }

    private void setCurrentLocation() {
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    myLocation = new LatLng(location.getLatitude(), location.getLongitude());
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCES_FINE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    activateMyLocation();
                } else {
                    deActivateMyLocation();
                }
                break;
        }
    }

    private void moveToClosestCenter() {
        LatLng closestPosition = pins.get(0).coords;

        double distance = UTIL.getDistanceBetweenTwoCoordonates(myLocation, closestPosition);

        for (Pin p : pins) {
            // check if this pin is closer to current device location
            double xDistance = UTIL.getDistanceBetweenTwoCoordonates(myLocation, p.coords);

            if (xDistance < distance) {
                closestPosition = p.coords;
                distance = xDistance;
            }
        }

        CameraPosition position = CameraPosition.builder()
                .target(closestPosition)
                .zoom(15)
                .tilt(30)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(position));
    }

    private void setupPins() {

        pins.add(
                new Pin(
                        getString(R.string.maps_my_home_title),
                        Double.parseDouble(getString(R.string.maps_my_home_lat)),
                        Double.parseDouble(getString(R.string.maps_my_home_lng)),
                        BitmapFactory.decodeResource(this.getResources(), R.drawable.cave_pin),
                        getString(R.string.maps_my_home_snippet)
                )
        );

        pins.add(
                new Pin(
                        getString(R.string.maps_faap_center_1_title),
                        Double.parseDouble(getString(R.string.maps_faap_center_1_lat)),
                        Double.parseDouble(getString(R.string.maps_faap_center_1_lng)),
                        BitmapFactory.decodeResource(this.getResources(), R.drawable.faap_pin),
                        getString(R.string.maps_faap_center_1_snippet)
                )
        );

        pins.add(
                new Pin(
                        getString(R.string.maps_faap_center_2_title),
                        Double.parseDouble(getString(R.string.maps_faap_center_2_lat)),
                        Double.parseDouble(getString(R.string.maps_faap_center_2_lng)),
                        BitmapFactory.decodeResource(this.getResources(), R.drawable.faap_pin),
                        getString(R.string.maps_faap_center_2_snippet)
                )
        );

        pins.add(
                new Pin(
                        getString(R.string.maps_faap_center_3_title),
                        Double.parseDouble(getString(R.string.maps_faap_center_3_lat)),
                        Double.parseDouble(getString(R.string.maps_faap_center_3_lng)),
                        BitmapFactory.decodeResource(this.getResources(), R.drawable.faap_pin),
                        getString(R.string.maps_faap_center_3_snippet)
                )
        );

        pins.add(
                new Pin(
                        getString(R.string.maps_faap_center_4_title),
                        Double.parseDouble(getString(R.string.maps_faap_center_4_lat)),
                        Double.parseDouble(getString(R.string.maps_faap_center_4_lng)),
                        BitmapFactory.decodeResource(this.getResources(), R.drawable.faap_pin),
                        getString(R.string.maps_faap_center_4_snippet)
                )
        );

    }
}
