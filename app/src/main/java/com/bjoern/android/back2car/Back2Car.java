package com.bjoern.android.back2car;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by bjoern on 18.09.17.
 *
 * @author <a href="mailto:mail@bjoern.cologne">Bjoern Gam</a>
 * @link <a href="http://bjoern.cologne">Webpage </a>
 * <p>
 * Description:
 */
class Back2Car implements Parcelable{

    private double mLatitude;
    private double mLongitude;

    Back2Car(double mLatitude, double mLongitude){
        setLatitude(mLatitude);
        setLongitude(mLongitude);
    }

    public static final Creator<Back2Car> CREATOR = new Creator<Back2Car>() {
        @Override
        public Back2Car createFromParcel(Parcel in) {
            return new Back2Car(in);
        }

        @Override
        public Back2Car[] newArray(int size) {
            return new Back2Car[size];
        }
    };


    private void setLatitude(double mLatitude){ this.mLatitude = mLatitude; }
    private void setLongitude (double mLongitude) {this.mLongitude = mLongitude;}

    double getLatitude(){return mLatitude;}
    double getLongitude() {return mLongitude;}

    private Back2Car(Parcel in) {
        mLatitude = in.readDouble();
        mLongitude = in.readDouble();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeDouble(mLatitude);
        parcel.writeDouble(mLongitude);
    }
}
