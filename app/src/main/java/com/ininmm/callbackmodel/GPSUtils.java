package com.ininmm.callbackmodel;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.RequiresPermission;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import io.reactivex.functions.Consumer;

/**
 * Created by User
 * on 2017/12/7.
 */

public class GPSUtils {
    private static GPSUtils mInstance;
    private LocationManager mManager;
    private String mProvider = LocationManager.GPS_PROVIDER;
    /*
    GPSUtils callback : Action1<Location> onLocationUpdate
     */
    private Consumer<Location> onLocationUpdate;
    private Timer mGPSTimer = new Timer();
    private Location mLocation;
    private AtomicBoolean isRequest = new AtomicBoolean(false);
    private AtomicInteger mResponseCount = new AtomicInteger(0);

    public static GPSUtils getInstance() {
        return mInstance;
    }

    private GPSUtils(Context context) {
        mManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    public static void init(Context context) {
        mInstance = new GPSUtils(context.getApplicationContext());
    }

    public boolean isEnable() {
        return mManager.isProviderEnabled(mProvider);
    }

    /**
     * 用AtomicBoolean使多線程時讓當前線程執行不會被打斷
     * 直到完成才由JVM選下一個線程執行
     * @return
     */
    public boolean isRequest() {
        return isRequest.get();
    }

    @RequiresPermission(anyOf = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION})
    public void start() {
        try {
            if (ActivityCompat.checkSelfPermission(BaseApplication.Companion.getInstance(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(BaseApplication.Companion.getInstance(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mManager.requestLocationUpdates(mProvider, 1000, 5, mListener);
            mGPSTimer.schedule(gpsTask(), 0, TimeUnit.SECONDS.toMillis(1));
            isRequest.set(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        onLocationUpdate = null;
        if (mManager != null) {
            mManager.removeUpdates(mListener);
        }
        if (mGPSTimer != null) {
            mGPSTimer.cancel();
            mGPSTimer = new Timer();
        }
        isRequest.set(false);
    }

    private LocationListener mListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            if (location != null) {
                mLocation = location;
                mResponseCount.set(0);
//                Log.i("GpsUtil", "Latitude: " + location.getLatitude()
//                        + "Longitude: " + location.getLongitude());
            }

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            if (mLocation != null && provider.equals(mProvider) && status != LocationProvider.AVAILABLE) {
                removeField();
            }
        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {
            if (mLocation != null && provider.equals(mProvider)) {
                removeField();
            }
        }
    };

    private void removeField() {
        Location tmp = new Location(mProvider);
        tmp.setTime(mLocation.getTime());
        tmp.setLatitude(mLocation.getLatitude());
        tmp.setLongitude(mLocation.getLongitude());
        mLocation = tmp;
    }

    private TimerTask gpsTask() {
        return new TimerTask() {
            @Override
            public void run() {
                if (mLocation != null && onLocationUpdate != null) {
                    try {
                        onLocationUpdate.accept(mLocation);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //超過3次沒傳資料就刪除
                    if (mResponseCount.get() > 3) {
                        removeField();
                    }
                    mResponseCount.set(mResponseCount.get() + 1);
                }
            }
        };
    }

    /**
     * CallBack Setter
     * @param onLocationUpdate
     */
    public void setOnLocationUpdate(Consumer<Location> onLocationUpdate) {
        this.onLocationUpdate = onLocationUpdate;
    }

    @RequiresPermission(anyOf = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION})
    public Location getLastLocation() {
        try {
            if (ActivityCompat.checkSelfPermission(BaseApplication.Companion.getInstance(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(BaseApplication.Companion.getInstance(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return null;
            }
//            Log.i("GPSUtils", "getLastLocation: Latitude = "
//                    + mManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLatitude() + ", Longitude = "
//                    + mManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLongitude());
            return mManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            return mManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
