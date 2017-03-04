package com.nimah.khiem.shoppingguide;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.hardware.Camera;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.github.jorgecastilloprz.FABProgressCircle;
import com.github.jorgecastilloprz.listeners.FABProgressListener;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.nimah.khiem.shoppingguide.Widget.ClipRevealFrame;
import com.ogaclejapan.arclayout.ArcLayout;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Detect extends AppCompatActivity implements SurfaceHolder.Callback, FABProgressListener {

    RelativeLayout rootLayout;
    ClipRevealFrame menuLayout;
    ArcLayout arcLayout;
    View centerItem;
    Button btnResult;
    View btnDetect;
    boolean isOpeningMenu=false;

    private FABProgressCircle fabProgressCircle;

    Camera mCamera;
    SurfaceView mSurfaceView;
    SurfaceHolder mSurfaceHolder;
    Camera.PictureCallback jpegCallback;
    RelativeLayout rlBox;
    View viewBBox;

    String url = "http://128.199.251.99/client_post";

    Bitmap bmp;

    Button btnArcYoutube, btnArcWiki, btnArcHome, btnArcMap, btnArcShare;
    int detectedID;

    static public Landmark DetectedLandmark;
    private Integer SurWidth, SurHeight, PicWidth, PicHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_detect);
        InitComponents();
        InitCameraCallback();
        InitListenner();
    }


    private void InitComponents() {
        mSurfaceView = (SurfaceView) findViewById(R.id.detect_surfaceView);
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(this);
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        rootLayout = (RelativeLayout) findViewById(R.id.detect_root_layout);
        menuLayout = (ClipRevealFrame) findViewById(R.id.menu_layout);
        arcLayout = (ArcLayout) findViewById(R.id.arc_layout);
        centerItem = findViewById(R.id.center_item);
        btnResult = (Button) findViewById(R.id.btn_result);

        fabProgressCircle = (FABProgressCircle) findViewById(R.id.fabProgressCircle);
        btnDetect = findViewById(R.id.fab_detect);
        rlBox = (RelativeLayout) findViewById(R.id.rl_boundingbox);
        /*viewBBox = new View(this);
        initBoudingBox();*/
        moveResultButton(0,getResources().getDisplayMetrics().heightPixels/2-70);

        btnArcYoutube = (Button) findViewById(R.id.btn_arc_youtube);
        btnArcWiki = (Button) findViewById(R.id.btn_arc_wiki);
        btnArcHome = (Button) findViewById(R.id.btn_arc_home);
        btnArcShare = (Button) findViewById(R.id.btn_arc_share);
        btnArcMap = (Button) findViewById(R.id.btn_arc_map);
    }

    private void InitListenner() {
        fabProgressCircle.attachListener(this);
        btnResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int x = (v.getLeft() + v.getRight()) / 2;
                int y = (v.getTop() + v.getBottom()) / 2;
                float radiusOfButton = 1f * v.getWidth() / 2f;
                float radiusFromButtonToRoot = (float) Math.hypot(
                        Math.max(x, rootLayout.getWidth() - x),
                        Math.max(y, rootLayout.getHeight() - y));
                if (isOpeningMenu) {
                    hideMenu(x, y, radiusFromButtonToRoot, radiusOfButton);

                } else {
                    showMenu(x, y, radiusOfButton, radiusFromButtonToRoot);
                }

                isOpeningMenu = !isOpeningMenu;
            }
        });

        // -----------------------------------------

        btnDetect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fabProgressCircle.show();
                try {
                    captureImage(view);

                } catch (IOException e) {
                    e.printStackTrace();
                    fabProgressCircle.hide();

                }
            }
        });

        // -----------------------------------------

        btnArcWiki.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(DetectedLandmark.getWikiUrl())));
            }
        });

        btnArcYoutube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(DetectedLandmark.getYoutubeUrl())));
            }
        });

        btnArcShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareLinkContent shareLinkContent = new ShareLinkContent.Builder()
                        .setContentUrl(Uri.parse(DetectedLandmark.getHomepageUrl()))
                        .build();
                ShareDialog.show((Activity) Detect.this, shareLinkContent);
            }
        });

        btnArcHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(DetectedLandmark.getHomepageUrl())));
            }
        });

        btnArcMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Detect.this, MapsActivity.class);
                intent.putExtra("position", detectedID);
                intent.putExtra("map_type",1);
                startActivity(intent);
            }
        });
    }


    /*--- Detect and Display ---*/
    public void onDetectionTaskDone(Landmark landmark) {
        DetectedLandmark = landmark;
/*        if (DetectedLandmark!=null) {
            Toast.makeText(Detect.this, landmark.getName() + " detected", Toast.LENGTH_SHORT).show();
            mappingCoordinate();

        } else {
            Toast.makeText(Detect.this, "null", Toast.LENGTH_SHORT).show();
        }*/
        fabProgressCircle.beginFinalAnimation();
        Picasso.with(this)
                .load(landmark.getImgUrl())
                .resize(70,70)
                .centerCrop()
                .into((ImageView) centerItem);


    }

    private void mappingCoordinate() {
        /*List<Vertex> vertices = DetectedLandmark.getVertices();
        for (Vertex v: vertices){
            float x = v.getX();
            float y = v.getY();
            Log.d("Toa do goc", String.valueOf(x) + " " + String.valueOf(y));
            x = (x/(float)PicWidth) * SurWidth;
            y = (y/(float) PicHeight) * SurHeight;
            v.setX((int) x);
            v.setY((int) y);
            Log.d("Toa do moi", String.valueOf(x) + " " + String.valueOf(y));
            Log.d("Toa do luu", String.valueOf(v.getX()) + " " + String.valueOf(v.getY()));
        }
        int h = Math.abs(vertices.get(0).getY() - vertices.get(3).getY());
        int w = Math.abs(vertices.get(0).getX() - vertices.get(1).getX());
        int x = vertices.get(0).getX();
        int y = vertices.get(0).getY();
        moveBoundingBox(x,y,w,h);*/

    }

    @Override
    public void onFABProgressAnimationEnd() {

        if (DetectedLandmark!=null){
            btnResult.setVisibility(View.VISIBLE);
            showStatus("Detected " + DetectedLandmark.getName());
        } else {
            showStatus("Nothing was detected");
        }
    }

    public void showStatus(String msg){
        Snackbar snackbar = Snackbar
                .make(rootLayout, msg, Snackbar.LENGTH_LONG);

        if (DetectedLandmark!=null){
            snackbar.setAction("VIEW", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    btnResult.performClick();
                }
            });
        }

        // Changing message text color
        View sbView = snackbar.getView();
        sbView.setBackgroundColor(Color.WHITE);
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(ContextCompat.getColor(this, R.color.colorAccent));
        snackbar.show();
    }

    public void initBoudingBox(){
        ShapeDrawable sd = new ShapeDrawable(new RectShape());
        sd.getPaint().setColor(0xFFFFFFFF);
        sd.getPaint().setStyle(Paint.Style.STROKE);
        sd.getPaint().setStrokeWidth(1);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            viewBBox.setBackground(sd);
        }

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(100, 100);
        params.setMargins(100, 100, 0, 0);
        rlBox.addView(viewBBox, params);
    }

    public void moveResultButton(int x, int y) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) btnResult.getLayoutParams();
        params.leftMargin = x;
        params.topMargin = y;
        btnResult.setLayoutParams(params);
    }

    public void moveBoundingBox(int x, int y, int w, int h){
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(w, h);
        params.setMargins(x, y, 0, 0);
        viewBBox.setLayoutParams(params);
    }


    @Override
    public void onBackPressed() {
        if (isOpeningMenu){
            btnResult.performClick();
            return;
        }
        super.onBackPressed();
    }

    /*--- Button Animation ---*/


    private void showMenu(int cx, int cy, float startRadius, float endRadius) {
        menuLayout.setVisibility(View.VISIBLE);
        btnResult.setVisibility(View.INVISIBLE);

        List<Animator> animList = new ArrayList<>();

        Animator revealAnim = createCircularReveal(menuLayout, cx, cy, startRadius, endRadius);
        revealAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        revealAnim.setDuration(200);

        animList.add(revealAnim);
        animList.add(createShowItemAnimator(centerItem));

        for (int i = 0, len = arcLayout.getChildCount(); i < len; i++) {
            animList.add(createShowItemAnimator(arcLayout.getChildAt(i)));
        }

        AnimatorSet animSet = new AnimatorSet();
        animSet.playSequentially(animList);
        animSet.start();
    }

    private void hideMenu(int cx, int cy, float startRadius, float endRadius) {
        List<Animator> animList = new ArrayList<>();

        for (int i = arcLayout.getChildCount() - 1; i >= 0; i--) {
            animList.add(createHideItemAnimator(arcLayout.getChildAt(i)));
        }

        animList.add(createHideItemAnimator(centerItem));

        Animator revealAnim = createCircularReveal(menuLayout, cx, cy, startRadius, endRadius);
        revealAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        revealAnim.setDuration(200);
        revealAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                menuLayout.setVisibility(View.INVISIBLE);
                btnResult.setVisibility(View.VISIBLE);
            }
        });

        animList.add(revealAnim);

        AnimatorSet animSet = new AnimatorSet();
        animSet.playSequentially(animList);
        animSet.start();

    }

    private Animator createShowItemAnimator(View item) {
        float dx = centerItem.getX() - item.getX();
        float dy = centerItem.getY() - item.getY();

        item.setScaleX(0f);
        item.setScaleY(0f);
        item.setTranslationX(dx);
        item.setTranslationY(dy);

        Animator anim = ObjectAnimator.ofPropertyValuesHolder(
                item,
                AnimatorUtils.scaleX(0f, 1f),
                AnimatorUtils.scaleY(0f, 1f),
                AnimatorUtils.translationX(dx, 0f),
                AnimatorUtils.translationY(dy, 0f)
        );

        anim.setInterpolator(new DecelerateInterpolator());
        anim.setDuration(50);
        return anim;
    }

    private Animator createHideItemAnimator(final View item) {
        final float dx = centerItem.getX() - item.getX();
        final float dy = centerItem.getY() - item.getY();

        Animator anim = ObjectAnimator.ofPropertyValuesHolder(
                item,
                AnimatorUtils.scaleX(1f, 0f),
                AnimatorUtils.scaleY(1f, 0f),
                AnimatorUtils.translationX(0f, dx),
                AnimatorUtils.translationY(0f, dy)
        );

        anim.setInterpolator(new DecelerateInterpolator());
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                item.setTranslationX(0f);
                item.setTranslationY(0f);
            }
        });
        anim.setDuration(50);
        return anim;
    }

    private Animator createCircularReveal(final ClipRevealFrame view, int x, int y, float startRadius,
                                          float endRadius) {
        final Animator reveal;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            reveal = ViewAnimationUtils.createCircularReveal(view, x, y, startRadius, endRadius);
        } else {
            view.setClipOutLines(true);
            view.setClipCenter(x, y);
            reveal = ObjectAnimator.ofFloat(view, "ClipRadius", startRadius, endRadius);
            reveal.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    view.setClipOutLines(false);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }
        return reveal;
    }



    /*--- Camera Settings ---*/

    private void InitCameraCallback() {
        jpegCallback = new Camera.PictureCallback() {

            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                FileOutputStream outStream = null;

                bmp = BitmapFactory.decodeByteArray(data,0,data.length);

                try {
                    callAPIServer();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                refreshCamera();
            }
        };
    }

    public void captureImage(View v) throws IOException {
        mCamera.takePicture(null, null, jpegCallback);
    }

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
            Log.d("aaaaa", String.valueOf(optimalSize.width) + String.valueOf(optimalSize.height));
            parameters.setPictureSize(1920, 1080);

            SurWidth = optimalSize.width;
            SurHeight = optimalSize.height;
            PicWidth = 1920;
            PicHeight = 1080;

            float x = 999;
            Log.d("Toa do moi", String.valueOf( (x/(float) PicWidth) * SurWidth));

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

    private void callAPIServer() throws IOException {

        new SendHttpRequestTask().execute(url);
        receiveAPIServer(1);
    }

    private void receiveAPIServer(int idx){
        onDetectionTaskDone(MainActivity.LandmarkList.get(idx));
        detectedID = idx;
    }

    private class SendHttpRequestTask extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... params) {
            String url = params[0];
//            Bitmap b = BitmapFactory.decodeResource(AnotherCamera.this.getResources(), R.drawable.charmeleon_001);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            bmp.compress(Bitmap.CompressFormat.JPEG, 0, baos);

            try {
                HttpClient client = new HttpClient(url);
                client.connectForMultipart();
//                client.addFormPart("param1", param1);
//                client.addFormPart("param2", param2);
                client.addFilePart("file", "choKhiem.jpg", baos.toByteArray());
                client.finishMultipart();

                String data = client.getResponse();
                Log.d("abcd", String.valueOf(data.charAt(4)));
            }
            catch(Throwable t) {
                t.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String data) {


        }

    }
}
