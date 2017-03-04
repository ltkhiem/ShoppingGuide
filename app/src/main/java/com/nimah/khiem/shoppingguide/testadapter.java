package com.nimah.khiem.shoppingguide;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

/**
 * Created by Khiem on 9/26/2016.
 */
public class testadapter extends ArrayAdapter<Landmark> {
    private Context context;
    private int rcID;
    private ArrayList<Landmark> landmark;

    public testadapter(Context context, int resource, ArrayList<Landmark> data) {
        super(context, resource, data);
        this.context = context;
        this.rcID = resource;
        this.landmark = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater layoutInflater = ((Activity) this.context).getLayoutInflater();
            convertView = layoutInflater.inflate(this.rcID, parent, false);
            return convertView;
        }
        return convertView;
    }
}
