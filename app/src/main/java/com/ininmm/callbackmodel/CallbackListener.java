package com.ininmm.callbackmodel;

import android.util.Log;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by User
 * on 2017/12/6.
 */

public class CallbackListener {
    interface OnClickListener {
        void onClick(int i);
    }

    private AtomicReference<OnClickListener> clickListener = new AtomicReference<>();

    public void setOnClickListener(OnClickListener onClickListener) {
        clickListener.set(onClickListener);
    }

    public void getOnClickListener(int i) {
//        clickListener.onClick(6);
        clickListener.get().onClick(i);
    }

    public void removeOnClickListener() {
//        this.clickListener = null;
        clickListener.set(null);
    }

    public void doClick() {
        Log.i("CallbackListener", "doClick: ");
        try {
            Log.i("CallbackListener", "Thread sleep: 5000");
            Thread.sleep(5000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        getOnClickListener(6);
    }
}
