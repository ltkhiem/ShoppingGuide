package com.nimah.khiem.shoppingguide;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.squareup.picasso.Picasso;

public class LandmarkDetails extends AppCompatActivity {
    ImageView ivLandmark, ivbWiki, ivbYoutube, ivbShare, ivbHomepage, ivbMap;
    TextView tvName;
    Landmark mLandmark;
    int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landmark_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        getSupportActionBar().hide();

        getIntentData();
        initComponents();
        initView();
        initListeners();
    }


    private void initComponents() {
        ivLandmark = (ImageView) findViewById(R.id.iv_detail_landmark_image);
        ivbWiki = (ImageView) findViewById(R.id.ivb_detail_wiki);
        ivbYoutube = (ImageView) findViewById(R.id.ivb_detail_youtube);
        ivbShare = (ImageView) findViewById(R.id.ivb_detail_share);
        ivbHomepage = (ImageView) findViewById(R.id.ivb_detail_homepage);
        ivbMap = (ImageView) findViewById(R.id.ivb_detail_map);
        tvName = (TextView) findViewById(R.id.tv_detail_name);
        tvName.setText(mLandmark.getName());
    }

    private void initView() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        Picasso.with(this)
                .load(mLandmark.getImgUrl())
                .resize(metrics.widthPixels, 485)
                .centerCrop()
                .into(ivLandmark);
    }

    private void initListeners() {
        ivbWiki.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(mLandmark.getWikiUrl())));
            }
        });

        ivbYoutube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(mLandmark.getYoutubeUrl())));
            }
        });

        ivbShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareLinkContent shareLinkContent = new ShareLinkContent.Builder()
                        .setContentUrl(Uri.parse(mLandmark.getHomepageUrl()))
                        .build();
                ShareDialog.show((Activity) LandmarkDetails.this, shareLinkContent);
            }
        });

        ivbHomepage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(mLandmark.getHomepageUrl())));
            }
        });

        ivbMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LandmarkDetails.this, MapsActivity.class);
                intent.putExtra("position", position);
                intent.putExtra("map_type",1);
                startActivity(intent);
            }
        });
    }

    public void getIntentData() {
        Intent intent = getIntent();
        intent.getIntExtra("chosen", position);
        mLandmark = MainActivity.LandmarkList.get(position);
    }
}
