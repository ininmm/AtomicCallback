package com.ininmm.callbackmodel;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.ininmm.callbackmodel.status.BicyclingStatus;

import java.util.Date;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MainService extends Service {
    private GPSUtils mGpsUtils;
    private int mGoodCount, mFailCount, mOver100Count, mOver200Count = 0;

    public MainService() {
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            mGpsUtils.stop();
            mGpsUtils = null;
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    public void rideDataUpdate() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (mGpsUtils != null && !mGpsUtils.isRequest()) {
                mGpsUtils.start();
//                io.reactivex.Observable.just(0)
//                        .subscribeOn(Schedulers.io())
//                        .subscribe(integer -> {
//                            if (ActivityCompat.checkSelfPermission(BaseApplication.Companion.getInstance(),
//                                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(BaseApplication.Companion.getInstance(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                                // TODO: Consider calling
//                                //    ActivityCompat#requestPermissions
//                                // here to request the missing permissions, and then overriding
//                                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                                //                                          int[] grantResults)
//                                // to handle the case where the user grants the permission. See the documentation
//                                // for ActivityCompat#requestPermissions for more details.
//                                return;
//                            }
//
//                        });
            }
        }

    }

    private Consumer<Location> onLocationListener = location -> {
        if (location != null) {
            GPSPoint point = new GPSPoint();
            point.setTimestamp(new Date(location.getTime()));
            point.setLatitude(location.getLatitude());
            point.setLongitude(location.getLongitude());
            if (location.hasSpeed() && location.getSpeed() >= 0) {
                double speed = Math.floor(location.getSpeed() * 3.6 * 10);
                 /*
                    if ()speed >=0) {
                        (int) speed;
                    } else {
                        speed = 0;
                    }
                 */
                point.setRawSpeed(speed >= 0 ? (int) speed : 0);
            }
            if (location.hasAltitude()) {
                point.setRawAltitude(location.getAltitude());
            }
            if (location.hasAccuracy()) {
                point.setHorizontalAccuracy((int) location.getAccuracy());
            }
            if (point.getHorizontalAccuracy() > 0 && point.getHorizontalAccuracy() <= 200) {
//                mCalculator.addGPSData(point);
            }

            //檢查GPS精準度
            if (point.getHorizontalAccuracy() == 0) {
                mFailCount++;
            } else if (point.getHorizontalAccuracy() > 0 && point.getHorizontalAccuracy() <= 100) {
                mGoodCount++;
            } else if (point.getHorizontalAccuracy() > 100 && point.getHorizontalAccuracy() <= 200) {
                mOver100Count++;
            } else {
                mOver200Count++;
            }
        }

    };

    public void stopAndNonSave() {
        stopSelf();
    }

    public void Start() {
        ((BaseApplication) getApplication()).setBikingStatus(BicyclingStatus.START);
//        improvePriority();
        GPSUtils.init(MainService.this);
        mGpsUtils = GPSUtils.getInstance();
        mGpsUtils.setOnLocationUpdate(onLocationListener);
        rideDataUpdate();
//        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//            Observable.just(0)
//                    .subscribeOn(AndroidSchedulers.mainThread())
//                    .subscribe(integer -> mGpsUtils.start());
//        }
    }

    public boolean Stop() {
        mGpsUtils.stop();
        return true;
    }

    public void Resume() {
        ((BaseApplication) getApplication()).setBikingStatus(BicyclingStatus.PAUSE);
    }

    public void Pause() {
        ((BaseApplication) getApplication()).setBikingStatus(BicyclingStatus.PAUSE);
    }

    private void setStatus(@BicyclingStatus int status) {
        ((BaseApplication) getApplication()).setBikingStatus(status);
    }

    private Location getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return null;
        }
        return mGpsUtils.getLastLocation();
    }




    @Override
    public IBinder onBind(Intent intent) {
        return new BikingServiceBinder();
    }

    public class BikingServiceBinder extends Binder {
        public MainService getMainService() {
            return MainService.this;
        }

        public void Start() {
            MainService.this.Start();
        }

        public boolean Stop() {
            return MainService.this.Stop();
        }

        public void Pause() {
            MainService.this.Pause();
        }

        public void Resume() {
            MainService.this.Resume();
        }

        public void stopAndNonSave() {
            MainService.this.stopAndNonSave();
        }

        public Location getCurrentLocation() {
            return MainService.this.getCurrentLocation();
        }

    /*
    callback
    start
    stop
    pause resume
     */
    }

}
