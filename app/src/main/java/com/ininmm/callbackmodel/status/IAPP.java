package com.ininmm.callbackmodel.status;

/**
 * Created by User
 * on 2017/12/7.
 */

public interface IAPP {
    @BicyclingStatus
    int getBikingStatus();
    void setBikingStatus(@BicyclingStatus int status);
}
