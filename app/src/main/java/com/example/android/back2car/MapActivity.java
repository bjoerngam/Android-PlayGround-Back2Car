package com.example.android.back2car;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

public class MapActivity extends Activity {


    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;

    private Button mDistance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CurrentPosition mCurrentPosition;
        setContentView(R.layout.activity_map);
        mDistance = findViewById(R.id.tvDistance);
        mCurrentPosition = new CurrentPosition(this);
        mCurrentPosition.setLocationManager();

        final GestureDetector listenerMap = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE &&
                        Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    Snackbar.make(findViewById(android.R.id.content), "Right to Left", Snackbar.LENGTH_LONG).show();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });

        String tvDistance = Double.toString(calculateDistance(getIntent().getExtras().getDouble("Latitude"),
                getIntent().getExtras().getDouble("Longitude"),
                mCurrentPosition.getmLatitude(),
                mCurrentPosition.getLongitue()));
        mDistance.setText(tvDistance);
        mDistance.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return listenerMap.onTouchEvent(motionEvent);
            }
        });

    }

    public double calculateDistance (double openStreetData_latitude, double openStreetData_longitude,
                                    double gpsData_latitude, double gpsData_longitude) {

        final int earth_radius = 6371;
        final double radian_translation = 180/Math.PI;

        double distance = Math.acos(Math.sin(openStreetData_latitude/radian_translation)
                * Math.sin(gpsData_latitude/radian_translation) + Math.cos(openStreetData_latitude/radian_translation)
                * Math.cos(gpsData_latitude/radian_translation)
                * Math.cos (openStreetData_longitude/radian_translation - gpsData_longitude/radian_translation)) * earth_radius;
        return (distance);
    }

}
