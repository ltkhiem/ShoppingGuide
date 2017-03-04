package com.nimah.khiem.shoppingguide;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.hardware.Camera;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.jorgecastilloprz.listeners.FABProgressListener;
import com.nimah.khiem.shoppingguide.Azimuth.AugmentedPOI;
import com.nimah.khiem.shoppingguide.Azimuth.MyCurrentAzimuth;
import com.nimah.khiem.shoppingguide.Azimuth.MyCurrentLocation;
import com.nimah.khiem.shoppingguide.Azimuth.OnAzimuthChangedListener;
import com.nimah.khiem.shoppingguide.Azimuth.OnLocationChangedListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Khiem on 24/09/15.
 */
public class ARDirection extends AppCompatActivity implements SurfaceHolder.Callback, OnLocationChangedListener, OnAzimuthChangedListener {

    Camera mCamera;
    SurfaceView mSurfaceView;
    SurfaceHolder mSurfaceHolder;
    Integer SurWidth, SurHeight, PicWidth, PicHeight;
    RelativeLayout rlRoot;
    ArrayList<View> infoBox = new ArrayList<>();

    private double mAzimuthReal = 0;
    private double mAzimuthTeoretical = 0;
    private static double AZIMUTH_ACCURACY = 30;
    private double mMyLatitude = 0;
    private double mMyLongitude = 0;

    private MyCurrentAzimuth myCurrentAzimuth;
    private MyCurrentLocation myCurrentLocation;

    ArrayList<AugmentedPOI> ranges = new ArrayList<>();
    ArrayList<Landmark> landmarks = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ardirection);

        InitComponents();
        LoadData();
        InitItemLayout();
    }

    private void InitComponents() {
        mSurfaceView = (SurfaceView) findViewById(R.id.ardir_surfaceView);
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(this);
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        myCurrentLocation = new MyCurrentLocation(this);
        myCurrentLocation.buildGoogleApiClient(this);
        myCurrentLocation.start();

        myCurrentAzimuth = new MyCurrentAzimuth(this, this);
        myCurrentAzimuth.start();

        rlRoot = (RelativeLayout) findViewById(R.id.ardir_root_layout);
    }

    private void LoadData(){
        landmarks = MainActivity.LandmarkList;
        for (int i=0; i<landmarks.size(); i++)
        {
            double midAzimuth = calculateTeoreticalAzimuth(landmarks.get(i));
            double minAzimuth = calculateAzimuthAccuracy(midAzimuth).get(0);
            double maxAzimuth = calculateAzimuthAccuracy(midAzimuth).get(0);
            ranges.add(new AugmentedPOI(i, midAzimuth, minAzimuth, maxAzimuth));
            Log.d(landmarks.get(i).getName(), String.valueOf(midAzimuth));
        }
    }

    private void InitItemLayout() {
        for (int i=0; i<landmarks.size(); i++) {
            View layout = LayoutInflater.from(this).inflate(R.layout.ardir_item_layout, rlRoot, false);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) layout.getLayoutParams();
            params.setMargins(i * 10, i * 10, 0, 0);
            rlRoot.addView(layout, params);
            infoBox.add(layout);

            ImageView ivAvatar = (ImageView) layout.findViewById(R.id.iv_ardir_avatar);
            Picasso.with(this)
                    .load(landmarks.get(i).getImgUrl())
                    .resize(60, 60)
                    .centerCrop()
                    .into(ivAvatar);
            TextView tvName = (TextView) layout.findViewById(R.id.tv_ardir_name);
            tvName.setText(landmarks.get(i).getName());

            final int finalI = i;
            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ARDirection.this, LandmarkDetails.class);
                    intent.putExtra("position", finalI);
                    startActivity(intent);
                }
            });
        }

    }

    public double calculateTeoreticalAzimuth(Landmark mlandmark) {
        double dX = mlandmark.getCoord().getLatitude() - mMyLatitude;
        double dY = mlandmark.getCoord().getLongitude() - mMyLongitude;

        double phiAngle;
        double tanPhi;
        double azimuth = 0;

        tanPhi = Math.abs(dY / dX);
        phiAngle = Math.atan(tanPhi);
        phiAngle = Math.toDegrees(phiAngle);

        if (dX > 0 && dY > 0) { // I quater
            return azimuth = phiAngle;
        } else if (dX < 0 && dY > 0) { // II
            return azimuth = 180 - phiAngle;
        } else if (dX < 0 && dY < 0) { // III
            return azimuth = 180 + phiAngle;
        } else if (dX > 0 && dY < 0) { // IV
            return azimuth = 360 - phiAngle;
        }

        return phiAngle;
    }

    private List<Double> calculateAzimuthAccuracy(double azimuth) {
        double minAngle = azimuth - AZIMUTH_ACCURACY;
        double maxAngle = azimuth + AZIMUTH_ACCURACY;
        List<Double> minMax = new ArrayList<Double>();

        if (minAngle < 0)
            minAngle += 360;

        if (maxAngle >= 360)
            maxAngle -= 360;

        minMax.clear();
        minMax.add(minAngle);
        minMax.add(maxAngle);

        return minMax;
    }

    @Override
    public void onLocationChanged(Location location) {
        mMyLatitude = location.getLatitude();
        mMyLongitude = location.getLongitude();
        updateTeoreticalAzimuth();


    }

    private void updateTeoreticalAzimuth() {
        for (int i=0; i<landmarks.size(); i++)
        {
            double midAzimuth = calculateTeoreticalAzimuth(landmarks.get(i));
            double minAzimuth = calculateAzimuthAccuracy(midAzimuth).get(0);
            double maxAzimuth = calculateAzimuthAccuracy(midAzimuth).get(1);
            AugmentedPOI r = ranges.get(i);
            r.setTeoreticalAzimuth(midAzimuth);
            r.setMinRange(minAzimuth);
            r.setMaxRange(maxAzimuth);
        }
    }

    @Override
    public void onAzimuthChanged(float azimuthChangedTo) {
        mAzimuthReal = azimuthChangedTo;

        for (int i=0; i<ranges.size(); i++) {
            View layout = infoBox.get(i);
            if (ranges.get(i).isInside(mAzimuthReal)) {
                Log.d("Entered", landmarks.get(i).getName());
                Log.d("Your azimuth", String.valueOf(mAzimuthReal));
                Log.d("Left - Right - Mid", String.valueOf(ranges.get(i).getMinRange()) + " - " + String.valueOf(ranges.get(i).getMaxRange()) + " - " + String.valueOf(ranges.get(i).getTeoreticalAzimuth()));

                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) layout.getLayoutParams();
                double diffAngle = Math.abs(mAzimuthReal - ranges.get(i).getTeoreticalAzimuth());
                int scrWidth = getResources().getDisplayMetrics().widthPixels;
                int leftMar = (int) ((scrWidth / 2) / (AZIMUTH_ACCURACY-10) * diffAngle) - params.width / 2;
                if (mAzimuthReal < ranges.get(i).getTeoreticalAzimuth())
                    leftMar += scrWidth / 2;
                else leftMar = scrWidth/2 - leftMar;
                params.setMargins(leftMar, i * 200 + 20, -400, 0);
                layout.setLayoutParams(params);
                layout.setVisibility(View.VISIBLE);

                float[] dist = new float[1];
                Location.distanceBetween(mMyLatitude, mMyLongitude,
                        landmarks.get(i).getCoord().getLatitude(), landmarks.get(i).getCoord().getLongitude(), dist);
                TextView tvDistance = (TextView) layout.findViewById(R.id.tv_ardir_distance);
                dist[0] = dist[0] / 1000;
                tvDistance.setText(String.format("%.1f km", dist[0]));
            } else layout.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onStop() {
        myCurrentAzimuth.stop();
        myCurrentLocation.stop();
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        myCurrentAzimuth.start();
        myCurrentLocation.start();
    }


    /**--- Setup for SurfaceView and Camera ---**/

    public Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio=(double)h / w;

        if (sizes == null) return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }

    public void refreshCamera() {
        if (mSurfaceHolder.getSurface() == null) {
            return;
        }

        try {
            mCamera.stopPreview();
        } catch (Exception e) {
        }

        try {
            Camera.Parameters parameters = mCamera.getParameters();
            List<Camera.Size> sizes = parameters.getSupportedPreviewSizes();
            Camera.Size optimalSize = getOptimalPreviewSize(sizes, getResources().getDisplayMetrics().widthPixels, getResources().getDisplayMetrics().heightPixels);
            parameters.setPreviewSize(optimalSize.width, optimalSize.height);
            parameters.setPictureSize(1920, 1080);

            SurWidth = optimalSize.width;
            SurHeight = optimalSize.height;
            PicWidth = 1920;
            PicHeight = 1080;

            mCamera.setParameters(parameters);
            mCamera.setPreviewDisplay(mSurfaceHolder);
            mCamera.startPreview();
        } catch (Exception e) {
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            mCamera = Camera.open();
        }
        catch (RuntimeException e) {
            System.err.println(e);
            return;
        }

        try {
            mCamera.setPreviewDisplay(mSurfaceHolder);
            mCamera.startPreview();
        }

        catch (Exception e) {
            System.err.println(e);
            return;
        }

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        refreshCamera();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mCamera.stopPreview();
        mCamera.release();
        mCamera = null;
    }
}