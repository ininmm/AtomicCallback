package com.ininmm.callbackmodel

import android.app.Application
import com.ininmm.callbackmodel.status.IAPP
import com.ininmm.callbackmodel.status.BicyclingStatus



/**
 * Created by User
 * on 2017/12/7.
 */
class BaseApplication: Application(), IAPP {
    @BicyclingStatus
    var mBikingStatus = BicyclingStatus.STOP

    override fun setBikingStatus(status: Int) {
        this.mBikingStatus = status;
    }

    override fun getBikingStatus(): Int {
        return mBikingStatus;
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: BaseApplication
            private set
    }
}