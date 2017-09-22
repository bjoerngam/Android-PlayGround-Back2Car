package com.example.android.back2car;

import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

/**
 * Created by bjoern on 18.09.17.
 *
 * @author <a href="mailto:mail@bjoern.cologne">Bjoern Gam</a>
 * @link <a href="http://bjoern.cologne">Webpage </a>
 * <p>
 * Description:
 */
public class CurrentPosition implements LocationListener {


    private LocationManager mLocationManager;
    private String mProvider;

    private double mLatitude;
    private double mLongitude;

    CurrentPosition(Activity mActivity){
        mLocationManager = (LocationManager) mActivity.getSystemService(Context.LOCATION_SERVICE);
    }

    public void setLocationManager(){
        Location location = null;
        Criteria criteria = new Criteria();
        mProvider = mLocationManager.getBestProvider(criteria, false);
        try{
            location = mLocationManager.getLastKnownLocation(mProvider);
        } catch (SecurityException exception) {exception.printStackTrace();}

        // Initialize the location fields
        if (location != null) {
            onLocationChanged(location);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        setLatitude(location.getLatitude());
        setLongitude(location.getLongitude());
    }

    private void setLongitude (double mLongitude) { this.mLongitude = mLongitude;}
    private void setLatitude (double mLatitude) { this.mLatitude = mLatitude;}

    public double getLongitue() {return mLongitude;}
    public double getmLatitude() {return mLatitude;}

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    public void locationManagerPause(){
        mLocationManager.removeUpdates(this);
    }

    public void locationManagerResume(){
        try{
            mLocationManager.requestLocationUpdates(mProvider, 400, 1, this);
        }catch (SecurityException exception){
            exception.printStackTrace();
        }
    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
