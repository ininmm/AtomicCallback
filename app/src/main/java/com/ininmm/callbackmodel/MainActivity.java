package com.ininmm.callbackmodel;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private CallbackListener mCallbackListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mCallbackListener = new CallbackListener();
        forTest();
        try {
            Log.i(TAG, "Thread sleep: 5000");
            Thread.sleep(5000);
            mCallbackListener.doClick();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void forTest() {
        mCallbackListener.setOnClickListener(onClickListener);
    }

    private CallbackListener.OnClickListener onClickListener = new CallbackListener.OnClickListener() {
        @Override
        public void onClick(int i) {
            Log.i(TAG, "onClick: " + i);
        }
    };
}
