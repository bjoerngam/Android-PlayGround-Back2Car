package com.example.android.back2car;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

public class MapActivity extends AppCompatActivity {


    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;

    private double set_longitude;
    private double set_latitude;

    private CurrentPosition mCurrentPosition;

    TextView mDistance;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        mDistance = (TextView) findViewById(R.id.tvDistance);
        set_latitude = getIntent().getDoubleExtra("Latitude", 0);
        set_longitude = getIntent().getDoubleExtra("Longitude" ,0);
        mCurrentPosition = new CurrentPosition(this);
        mCurrentPosition.setLocationManager();

        final GestureDetector listenerMap = new GestureDetector(getBaseContext(), new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE &&
                        Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    Snackbar.make(findViewById(android.R.id.content), "Right to Left", Snackbar.LENGTH_LONG).show();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    return true;
                }

                return false;
            }
        });



        String tvDistance = Double.toString(calculateDistance(set_latitude, set_longitude,
                mCurrentPosition.getmLatitude(), mCurrentPosition.getLongitue()));

        mDistance.setText(tvDistance);

        mDistance.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return listenerMap.onTouchEvent(motionEvent);
            }
        });

    }

    private double calculateDistance (double openStreetData_latitude, double openStreetData_longitude,
                                    double gpsData_latitude, double gpsData_longitude) {

        final int earth_radius = 6371;
        final double radian_translation = 180/Math.PI;

        double distance = Math.acos(Math.sin(openStreetData_latitude/radian_translation)
                * Math.sin(gpsData_latitude/radian_translation) + Math.cos(openStreetData_latitude/radian_translation)
                * Math.cos(gpsData_latitude/radian_translation)
                * Math.cos (openStreetData_longitude/radian_translation - gpsData_longitude/radian_translation)) * earth_radius;

        Log.i("Distance", Double.toString(distance));
        Log.i("Distance:", Double.toString(openStreetData_latitude) + " " + Double.toString(openStreetData_longitude)
                + " GPS:" + Double.toString(gpsData_latitude) + " " + Double.toString(gpsData_longitude));
        return (distance);
    }



}
