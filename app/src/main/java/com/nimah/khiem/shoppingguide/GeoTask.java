package com.nimah.khiem.shoppingguide;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Khiem on 7/4/2016.
 */
public class GeoTask extends AsyncTask<String, Void, JSONObject> {
    ProgressDialog pd;
    Context mContext;
    Double duration;
    Geo geo1;

    public GeoTask(Context mContext) {
        this.mContext = mContext;
        geo1= (Geo) mContext;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pd=new ProgressDialog(mContext);
        pd.setMessage("Loading");
        pd.setCancelable(false);
        pd.show();
    }

    @Override
    protected void onPostExecute(JSONObject aDouble) {
        super.onPostExecute(aDouble);
        if(aDouble!=null)
        {
            geo1.getResult(aDouble);
            pd.dismiss();
        }
        else
            Toast.makeText(mContext, "Error4!Please Try Again wiht proper values", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected JSONObject doInBackground(String... params) {
        try {
            URL url=new URL(params[0]);
            HttpURLConnection con= (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.connect();
            int statuscode=con.getResponseCode();
            if(statuscode==HttpURLConnection.HTTP_OK)
            {
                BufferedReader br=new BufferedReader(new InputStreamReader(con.getInputStream()));
                StringBuilder sb=new StringBuilder();
                String line=br.readLine();
                while(line!=null)
                {
                    sb.append(line);
                    line=br.readLine();
                }
                String json=sb.toString();
                Log.d("JSON", json);
                JSONObject root=new JSONObject(json);
                return root;

            }
        } catch (MalformedURLException e) {
            Log.d("error", "error1");
        } catch (IOException e) {
            Log.d("error", "error2");
        } catch (JSONException e) {
            Log.d("error","error3");
        }


        return null;
    }
    interface Geo{
        public void getResult(JSONObject jsonObject);
    }

}
