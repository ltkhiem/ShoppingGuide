package com.nimah.khiem.shoppingguide.Azimuth;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * Created by krzysztofjackowski on 24/09/15.
 */
public class MyCurrentAzimuth implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor sensor;
    private float azimuthTo = 0;
    private OnAzimuthChangedListener mAzimuthListener;
    Context mContext;

    public MyCurrentAzimuth(OnAzimuthChangedListener azimuthListener, Context context) {
        mAzimuthListener = azimuthListener;
        mContext = context;
    }

    public void start(){
        sensorManager = (SensorManager) mContext.getSystemService(mContext.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        sensorManager.registerListener(this, sensor,
                SensorManager.SENSOR_DELAY_UI);
    }

    public void stop(){
        sensorManager.unregisterListener(this);
    }

    public void setOnShakeListener(OnAzimuthChangedListener listener) {
        mAzimuthListener = listener;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

       //float[] orientation = new float[3];
        //float[] rMat = new float[9];
       // SensorManager.getRotationMatrixFromVector(rMat, event.values);

        //azimuthTo = (int) ( Math.toDegrees( SensorManager.getOrientation( rMat, orientation )[0] ) + 360 ) % 360;
        azimuthTo = event.values[0] + 90;
        if (azimuthTo>360) azimuthTo-=360;

        mAzimuthListener.onAzimuthChanged(azimuthTo);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
