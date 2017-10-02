package com.example.android.back2car;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends Activity {

    private CurrentPosition mCurrentPosition;
    private Back2Car mBack2Car;
    private boolean isClicked = false;

    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;

    private Button mButton;

    private SharedPreferences sharedpreferences;
    private boolean isSharedPreferences = false;
    private final String STORED_LATITUDE = "Latitude";
    private final String STORED_LONGITUDE = "Longitude";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String APP_NAME = "Back2Car";
        ImageView mImageViewSteps;
        checkSecurity();

        sharedpreferences = getSharedPreferences(APP_NAME, Context.MODE_PRIVATE);

        mButton = findViewById(R.id.btSetPosition);
        mButton.setElevation(16);
        mImageViewSteps = findViewById(R.id.ivSteps);

        mImageViewSteps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mBack2Car != null) {
                    Intent intent = new Intent(getApplicationContext(), MapActivity.class);
                    intent.putExtra(STORED_LATITUDE, mBack2Car.getLatitude());
                    intent.putExtra(STORED_LONGITUDE, mBack2Car.getLongitude());
                    startActivity(intent);
                } else {
                    Snackbar.make(findViewById(android.R.id.content),
                            "Currently no position set!", Snackbar.LENGTH_LONG).show();
                }

            }
        });

        mCurrentPosition = new CurrentPosition(this);
        mCurrentPosition.setLocationManager();

      final GestureDetector listener = new GestureDetector(getBaseContext(), new GestureDetector.SimpleOnGestureListener() {

          @Override
          public boolean onDoubleTap(MotionEvent e) {
              if (!isClicked) {
                  if (isSharedPreferences) {
                      String latitude = sharedpreferences.getString(STORED_LATITUDE, null);
                      String longitude = sharedpreferences.getString(STORED_LONGITUDE, null);
                      Snackbar.make(findViewById(android.R.id.content), "Found settings", Snackbar.LENGTH_LONG).show();
                      mButton.setBackground(getResources().getDrawable(R.drawable.circle_set));
                      mBack2Car = new Back2Car(Double.valueOf(latitude), Double.valueOf(longitude));
                      isClicked = true;
                      return true;
                  }else {
                      mButton.setBackground(getResources().getDrawable(R.drawable.circle_set));
                      mBack2Car = new Back2Car(mCurrentPosition.getmLatitude(), mCurrentPosition.getLongitue());
                      Snackbar.make(findViewById(android.R.id.content), "Position set.", Snackbar.LENGTH_LONG).show();
                      isClicked = true;
                      isSharedPreferences = true;
                      return true;
                  }
              } else {
                  Snackbar.make(findViewById(android.R.id.content), "Bring me Back", Snackbar.LENGTH_LONG).show();
                  return true;
              }
          }

          @Override
          public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
               if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE &&
                      Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                  if (mBack2Car != null){

                      SharedPreferences.Editor editor = sharedpreferences.edit();
                      editor.putString(STORED_LATITUDE, Double.toString(mBack2Car.getLatitude()));
                      editor.putString(STORED_LONGITUDE, Double.toString(mBack2Car.getLongitude()));
                      editor.apply();

                      Intent intent = new Intent(getApplicationContext(), Back2CarMap.class);
                      intent.putExtra(STORED_LATITUDE, mBack2Car.getLatitude());
                      intent.putExtra(STORED_LONGITUDE, mBack2Car.getLongitude());
                      startActivity(intent);
                      return true;
                  }
                  return true;
              }
              return false;
          }
      });

        mButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent) {
                return listener.onTouchEvent(motionEvent);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        mCurrentPosition.locationManagerPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCurrentPosition.locationManagerResume();
    }

    public void checkSecurity(){
        int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 123;
        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION },
                    REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
        }
    }

}
