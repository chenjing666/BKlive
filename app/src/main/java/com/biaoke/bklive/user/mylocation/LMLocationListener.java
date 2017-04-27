package com.biaoke.bklive.user.mylocation;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by hasee on 2017/4/27.
 */

public class LMLocationListener implements LocationListener {

    Location mLastLocation;
    boolean mValid = false;

    @Override
    public void onLocationChanged(Location newLocation) {
        if (newLocation.getLatitude() == 0.0
                && newLocation.getLongitude() == 0.0) {
            // Hack to filter out 0.0,0.0 locations
            return;
        }
        if (!mValid) {
            Log.d(Constants.TAG, "Got first location.");
        }
        if (mLastLocation == null) {
            mLastLocation = newLocation;
        } else {
            mLastLocation.set(newLocation);
        }
        Log.d(Constants.TAG, "the newLocation is " + newLocation.getLongitude() + "x" + newLocation.getLatitude());
        mValid = true;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle bundle) {
        switch (status) {
            case LocationProvider.OUT_OF_SERVICE:
            case LocationProvider.TEMPORARILY_UNAVAILABLE: {
                mValid = false;
                break;
            }
        }
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d(Constants.TAG, " support current " + provider);
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d(Constants.TAG, "no support current " + provider);
        mValid = false;
    }


    //获得当前地理位置
    public Location current() {
        return mValid ? mLastLocation : null;
    }

}
