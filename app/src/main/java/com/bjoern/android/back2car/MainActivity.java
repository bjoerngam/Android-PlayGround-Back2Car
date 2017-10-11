package com.bjoern.android.back2car;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;

public class MainActivity extends Activity {

    private CurrentPosition mCurrentPosition;
    private Back2Car mBack2Car;
    private boolean isClicked = false;

    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;

    private Button mButton;
    private RelativeLayout helpRelativeLayout;

    private SharedPreferences sharedpreferences;
    private boolean isSharedPreferences = false;
    private final String STORED_LATITUDE = "Latitude";
    private final String STORED_LONGITUDE = "Longitude";
    private final String STORED_VEHICLE = "Vehicle";

    private String vehicle_number;

    private final String MODE_CAR = "mode=driving";
    private final String MODE_WALK = "mode=walking";
    private final String MODE_BICYCLING = "mode=bicycling";
    private final String MODE_TRAIN = "mode=transit";

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

        helpRelativeLayout = findViewById(R.id.rlHelp);

        if (isFirstTime()) {
            helpRelativeLayout.setVisibility(View.INVISIBLE);
        }

        mImageViewSteps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mBack2Car != null) {
                    Intent intent = new Intent(getApplicationContext(), DistanceActivity.class);
                    intent.putExtra(STORED_LATITUDE, mBack2Car.getLatitude());
                    intent.putExtra(STORED_LONGITUDE, mBack2Car.getLongitude());
                    intent.putExtra(STORED_VEHICLE, vehicle_number);
                    startActivity(intent);
                } else {
                    Snackbar.make(findViewById(android.R.id.content),
                            getString(R.string.snackbar_error),
                            Snackbar.LENGTH_LONG).show();
                }

            }
        });
        mCurrentPosition = new CurrentPosition(this);
        if(hasSecurity()) {
            mCurrentPosition.setLocationManager();
        }

      final GestureDetector listener = new GestureDetector(getBaseContext(), new GestureDetector.SimpleOnGestureListener() {

          @Override
          public boolean onDoubleTap(MotionEvent e) {
              if (!isClicked) {
                  if (isSharedPreferences) {
                      String latitude = sharedpreferences.getString(STORED_LATITUDE, null);
                      String longitude = sharedpreferences.getString(STORED_LONGITUDE, null);
                      Snackbar.make(findViewById(android.R.id.content), getString(R.string.shared_preferences_found)
                              , Snackbar.LENGTH_LONG).show();
                      mButton.setBackground(getResources().getDrawable(R.drawable.circle_set));
                      mBack2Car = new Back2Car(Double.valueOf(latitude), Double.valueOf(longitude));
                      isClicked = true;
                      return true;
                  }else {
                      mButton.setBackground(getResources().getDrawable(R.drawable.circle_set));
                      mBack2Car = new Back2Car(mCurrentPosition.getLatitude(), mCurrentPosition.getLongitude());
                      Snackbar.make(findViewById(android.R.id.content), getString(R.string.snackbar_position_set),
                              Snackbar.LENGTH_LONG).show();
                      isClicked = true;
                      isSharedPreferences = true;
                      return true;
                  }
              } else {
                  Snackbar.make(findViewById(android.R.id.content), getString(R.string.snackbar_back_to_start)
                          , Snackbar.LENGTH_LONG).show();
                  getVehicleDialogBox();
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
                      intent.putExtra(STORED_VEHICLE, vehicle_number);
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
        if (hasSecurity()) {
            mCurrentPosition.setLocationManager();
            mCurrentPosition.locationManagerPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(hasSecurity()) {
            mCurrentPosition.setLocationManager();
            mCurrentPosition.locationManagerResume();
        }
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

    public boolean hasSecurity(){
        return (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED);
    }

    public void getVehicleDialogBox() {

        final AlertDialog.Builder vehicleDialog =
                new AlertDialog.Builder(MainActivity.this, android.R.style.Theme_DeviceDefault_Light_Dialog);
        LayoutInflater factory = LayoutInflater.from(this);
        final ViewGroup nullParent = null;
        final View f = factory.inflate(R.layout.dialogbox_vehicle, nullParent);

        vehicleDialog.setView(f);
        vehicleDialog.setTitle(getString(R.string.dialogbox_vehicle));

        RadioButton mRadioButtonCar = f.findViewById(R.id.rdCar);
        RadioButton mRadioButtonTransit = f.findViewById(R.id.rdTransit);
        RadioButton mRadioButtonBike = f.findViewById(R.id.rdBike);
        RadioButton mRadioButtonWalk = f.findViewById(R.id.rdWalk);

        final AlertDialog dialog = vehicleDialog.show();

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()){
                    case R.id.rdCar:
                        vehicle_number = MODE_CAR;
                        dialog.dismiss();
                        break;
                    case R.id.rdBike:
                        vehicle_number = MODE_BICYCLING;
                        dialog.dismiss();
                        break;

                    case R.id.rdWalk:
                        vehicle_number = MODE_WALK;
                        dialog.dismiss();
                        break;
                    case R.id.rdTransit:
                        vehicle_number = MODE_TRAIN;
                        dialog.dismiss();
                        break;
                }

            }
        };

        mRadioButtonBike.setOnClickListener(onClickListener);
        mRadioButtonCar.setOnClickListener(onClickListener);
        mRadioButtonWalk.setOnClickListener(onClickListener);
        mRadioButtonTransit.setOnClickListener(onClickListener);

        dialog.show();
    }

    public boolean isFirstTime(){

        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        boolean ranBefore = preferences.getBoolean("RanBefore", false);

        if (!ranBefore) {

            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("RanBefore", true);
            editor.apply();
            helpRelativeLayout.setVisibility(View.VISIBLE);
            helpRelativeLayout.setOnTouchListener(new View.OnTouchListener(){

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    helpRelativeLayout.setVisibility(View.INVISIBLE);
                    return false;
                }

            });


        }
        return ranBefore;

    }

}
