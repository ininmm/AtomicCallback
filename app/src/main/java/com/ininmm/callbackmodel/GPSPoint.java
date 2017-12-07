package com.ininmm.callbackmodel;

import android.location.Location;

import java.util.Date;

/**
 * Created by User
 * on 2017/12/7.
 */

public class GPSPoint {

    /**
     * 此GPS位置的UTC時戳
     */
    private Date timestamp = new Date();

    /**
     * 緯度
     * 正常範圍：-90 ~ +90
     */
    private double latitude = -999;

    /**
     * 經度
     * 正常範圍：-180 ~ +180
     */
    private double longitude = -999;

    /**
     * GPS速度
     * 單位：0.1 km/h
     */
    private int rawSpeed = 0;

    /**
     * GPS海拔
     * -9999表示此欄位無效
     * 單位：公尺
     */
    private double rawAltitude = -9999;

    /**
     * GPS水平精準度
     * -1表示此欄位無效
     */
    private int horizontalAccuracy = -1;

    /**
     * 廢物欄位 , Android用不到
     * GPS海拔精準度
     * -1表示此欄位無效
     */
    private final int vertialAccuracy = -1;

    public Date getTimestamp() {
        return timestamp;
    }

    public GPSPoint setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public double getLatitude() {
        return latitude;
    }

    public GPSPoint setLatitude(double latitude) {
        this.latitude = latitude;
        return this;
    }

    public double getLongitude() {
        return longitude;
    }

    public GPSPoint setLongitude(double longitude) {
        this.longitude = longitude;
        return this;
    }

    public int getRawSpeed() {
        return rawSpeed;
    }

    public GPSPoint setRawSpeed(int rawSpeed) {
        this.rawSpeed = rawSpeed;
        return this;
    }

    public double getRawAltitude() {
        return rawAltitude;
    }

    public GPSPoint setRawAltitude(double rawAltitude) {
        this.rawAltitude = rawAltitude;
        return this;
    }

    public int getHorizontalAccuracy() {
        return horizontalAccuracy;
    }

    public GPSPoint setHorizontalAccuracy(int horizontalAccuracy) {
        this.horizontalAccuracy = horizontalAccuracy;
        return this;
    }

    public int getVertialAccuracy() {
        return vertialAccuracy;
    }

    public double distanceBetween(GPSPoint point) {
        float[] result = new float[1];
        Location.distanceBetween(latitude, longitude, point.latitude, point.longitude, result);
        return result[0];
    }
}
