package com.example.android.back2car;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

public class Back2CarMap extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_back2_car_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng currentPosition = new LatLng (getIntent().getExtras().getDouble("Latitude"),
                getIntent().getExtras().getDouble("Longitude"));

        BitmapDescriptor infoBitmap = BitmapDescriptorFactory.fromResource(R.drawable.car);

        MarkerOptions infoScreenMarker = new MarkerOptions()
                .position(currentPosition)
                .icon(infoBitmap);

        LatLngBounds bounds = new LatLngBounds.Builder()
                .include(currentPosition)
                .build();

        mMap.addMarker(new MarkerOptions().position(currentPosition).title("Startpoint"));
       // mMap.moveCamera(CameraUpdateFactory.newLatLng(currentPosition));
        mMap.addMarker(infoScreenMarker);
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 5));

    }
}
