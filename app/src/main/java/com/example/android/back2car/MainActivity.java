package com.example.android.back2car;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

    private CurrentPosition mCurrentPosition;
    private Back2Car mBack2Car;
    private boolean isClicked = false;

    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;

    private Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkSecurity();
        mButton = findViewById(R.id.btSetPosition);
        mButton.setElevation(16);
        mCurrentPosition = new CurrentPosition(this);
        mCurrentPosition.setLocationManager();

      final GestureDetector listener = new GestureDetector(getBaseContext(), new GestureDetector.SimpleOnGestureListener() {

          @Override
          public boolean onDoubleTap(MotionEvent e) {
              if (!isClicked) {
                  mButton.setBackground(getResources().getDrawable(R.drawable.circle_set));
                  mBack2Car = new Back2Car(mCurrentPosition.getmLatitude(), mCurrentPosition.getLongitue());
                  Snackbar.make(findViewById(android.R.id.content), Double.toString(mCurrentPosition.getLongitue()) + " " +
                          Double.toString(mCurrentPosition.getmLatitude()), Snackbar.LENGTH_LONG).show();
                  isClicked = true;

                  return true;
              } else {
                  Snackbar.make(findViewById(android.R.id.content), "Bring me Back", Snackbar.LENGTH_LONG).show();
                return true;
              }
          }

          @Override
          public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
              Snackbar.make(findViewById(android.R.id.content), "Remove.", Snackbar.LENGTH_LONG).show();
              mButton.setBackground(getResources().getDrawable(R.drawable.circle));
              if (mBack2Car != null) {
                  mBack2Car.setLongitude(0.00);
                  mBack2Car.setLatitude(0.00);
                  isClicked = false;
              }
              return true;
          }

          @Override
          public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
              if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE &&
                      Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                  Snackbar.make(findViewById(android.R.id.content), "Right to Left", Snackbar.LENGTH_LONG).show();
                  //From Right to Left
                  return true;
              }  else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE &&
                      Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                  if (mBack2Car != null){
                      Intent intent = new Intent(getApplicationContext(), MapActivity.class);
                      intent.putExtra("Latitude", mBack2Car.getLatitude());
                      intent.putExtra("Longitude", mBack2Car.getLongitude());
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
