package com.ininmm.callbackmodel;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.crashlytics.android.Crashlytics;
import com.ininmm.callbackmodel.status.BicyclingStatus;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import io.fabric.sdk.android.Fabric;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private CallbackListener mCallbackListener;
    private MainService.BikingServiceBinder mBikingServiceBinder;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);
        button = findViewById(R.id.btn_click);
        mCallbackListener = new CallbackListener();

        bindService();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBikingServiceBinder.Start();
                forTest();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void forTest() {
        mCallbackListener.setOnClickListener(onClickListener);
        try {
            mCallbackListener.doClick();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private CallbackListener.OnClickListener onClickListener = new CallbackListener.OnClickListener() {
        @Override
        public void onClick(int i) {
            Log.i(TAG, "onClick: " + i);
            Observable.just(0)
                    .delay(3000, TimeUnit.SECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(integer -> {
                        Log.i(TAG, "onClick: " +Thread.currentThread().getName());
                        Location location = mBikingServiceBinder.getCurrentLocation();
                        Log.i(TAG, "longitude: " + location.getLongitude());
//                        Log.i(TAG, "longitude: " + mBikingServiceBinder.getCurrentLocation().getLongitude());
//                        Log.i(TAG, "latitude: " + mBikingServiceBinder.getCurrentLocation().getLatitude());
                    });
        }

        @Override
        public void onGPSTracker(Location location) {

        }
    };

    private void setBicyclingStatus(@BicyclingStatus int status) {
        ((BaseApplication) getApplication()).setBikingStatus(status);
    }

    private @BicyclingStatus int getStatus() {
        return ((BaseApplication) getApplication()).getBikingStatus();
    }


    private void bindService() {
        bindService(new Intent(getApplicationContext(), MainService.class), mServiceConnection, BIND_AUTO_CREATE);
    }

    private void stopService() {
        stopService(new Intent(getApplicationContext(), MainService.class));
        mBikingServiceBinder = null;
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            if (service instanceof  MainService.BikingServiceBinder) {
                mBikingServiceBinder = (MainService.BikingServiceBinder) service;
//                mBikingServiceBinder.Resume();
                try {
                    setBicyclingStatus(BicyclingStatus.STOP);
//                    if (mBikingServiceBinder != null) {
//                        mBikingServiceBinder.Stop();
//                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBikingServiceBinder = null;
        }
    };


}
