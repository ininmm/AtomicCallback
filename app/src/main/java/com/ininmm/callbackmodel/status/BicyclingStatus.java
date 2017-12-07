package com.ininmm.callbackmodel.status;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by User
 * on 2017/12/7.
 */

@Retention(RetentionPolicy.SOURCE)
@IntDef({BicyclingStatus.STOP, BicyclingStatus.START, BicyclingStatus.PAUSE, BicyclingStatus.UNKNOWN})
public @interface BicyclingStatus {

    int STOP = 0;
    int START = 1;
    int PAUSE = 2;
    int UNKNOWN = 3;
}

