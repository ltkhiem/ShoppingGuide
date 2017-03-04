package com.nimah.khiem.shoppingguide;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatCallback;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidviewhover.BlurLayout;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Khiem on 9/25/2016.
 */
public class LandMarkListAdapter extends ArrayAdapter<Landmark>{
    private Context context;
    private int rcId;
    private ArrayList<Landmark> LandmarkList;

    public LandMarkListAdapter(Context context, int resource, ArrayList<Landmark> data) {
        super(context, resource, data);
        this.context = context;
        this.rcId = resource;
        this.LandmarkList = data;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;
        if (convertView == null) {
            LayoutInflater layoutInflater = ((Activity) this.context).getLayoutInflater();
            convertView = layoutInflater.inflate(this.rcId, parent, false);
            holder = new ViewHolder(convertView, this.context);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final Landmark landmark = getItem(position);
        Picasso.with(context)
                .load(landmark.getImgUrl())
                .resize(200, 200)
                .centerCrop()
                .into(holder.ivLandmarkImg);

        holder.tvLandmarkName.setText(landmark.getName());

        holder.ivbShowDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,LandmarkDetails.class);
                intent.putExtra("chosenPosition", position);
                context.startActivity(intent);
            }
        });


        holder.ivbShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* ShareLinkContent shareLinkContent = new ShareLinkContent.Builder()
                        .setContentUrl(Uri.parse(landmark.getDetailUrl()))
                        .build();
                ShareDialog.show((Activity) context, shareLinkContent);*/
            }
        });

        return convertView;
    }

    static class ViewHolder{
        Context mContext;
        private ImageView ivLandmarkImg;
        private TextView tvLandmarkName;
        private BlurLayout blItem;
        private ImageView ivbShowDetail;
        private ImageView ivbShare;

        public ViewHolder(View convertView, Context context) {
            mContext = context;
            ivLandmarkImg = (ImageView) convertView.findViewById(R.id.iv_landmark_image);

            BlurLayout.setGlobalDefaultDuration(450);
            blItem = (BlurLayout) convertView.findViewById(R.id.bl_image);
            View hover = LayoutInflater.from(mContext).inflate(R.layout.landmark_item_info, null);
            if (hover != null) {
                tvLandmarkName = (TextView) hover.findViewById(R.id.tv_name);
                ivbShowDetail = (ImageView) hover.findViewById(R.id.ivb_show_detail);
                ivbShare = (ImageView) hover.findViewById(R.id.ivb_share);
                tvLandmarkName.setMovementMethod(new ScrollingMovementMethod());

                blItem.setHoverView(hover);
                blItem.addChildAppearAnimator(hover, R.id.ivb_show_detail, Techniques.SlideInLeft);
                blItem.addChildAppearAnimator(hover, R.id.ivb_share, Techniques.SlideInRight);

                blItem.addChildDisappearAnimator(hover, R.id.ivb_show_detail, Techniques.SlideOutLeft);
                blItem.addChildDisappearAnimator(hover, R.id.ivb_share, Techniques.SlideOutRight);

                blItem.addChildAppearAnimator(hover, R.id.tv_name, Techniques.BounceIn);
                blItem.addChildDisappearAnimator(hover, R.id.tv_name, Techniques.FadeOutUp);

            }
        }
    }
}
