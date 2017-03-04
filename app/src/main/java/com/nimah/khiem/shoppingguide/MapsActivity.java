package com.nimah.khiem.shoppingguide;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.nimah.khiem.shoppingguide.Azimuth.MyCurrentLocation;
import com.nimah.khiem.shoppingguide.Azimuth.OnLocationChangedListener;
import com.nimah.khiem.shoppingguide.Modules.DirectionFinder;
import com.nimah.khiem.shoppingguide.Modules.DirectionFinderListener;
import com.nimah.khiem.shoppingguide.Modules.GPSTracker;
import com.nimah.khiem.shoppingguide.Modules.Route;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, DirectionFinderListener, GeoTask.Geo {

    private GoogleMap mMap;
    private float PlaceLat;
    private float PlaceLng;
    private LatLng eventPoint;
    private View contentView;
    private Button btnInvite;
    private MapWrapperLayout mapWrapperLayout;
    private OnInterInfoWindowTouchListener lsClick;

    private Button btnFindPath;
    private EditText etStartPos;
    private EditText etDestination;
    private List<Marker> StartPosMarkers = new ArrayList<>();
    private List<Marker> DestinationMarkers = new ArrayList<>();
    private List<Polyline> PolylinePaths = new ArrayList<>();
    private ProgressDialog progressDialog;
    private Button btnGetHere;
    private OnInterInfoWindowTouchListener ghClick;
    private int map_type;
    private String eventName;
    private String eventAddress;
    private ArrayList<Landmark> landmarks;
    private String strStartPos;
    private String strDestination;
    private Integer personID;
    private ArrayList<Integer> Dist;
    private LatLng MyLoc;
    private int position;
    private Landmark pLandmark;
    private TextView tvMarkerName;
    private TextView tvMarkerInfo1;
    private TextView tvMarkerInfo2;
    private ImageView ivAvatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mapWrapperLayout = (MapWrapperLayout) findViewById(R.id.map_wrapper);

        getData();
        initComponents();
        initListeners();
    }

    private void initMyLocation() {
        if (mMap.getMyLocation()!=null)
            MyLoc = new LatLng(mMap.getMyLocation().getLatitude(), mMap.getMyLocation().getLongitude());
    }

    private void initComponents() {
        contentView = LayoutInflater.from(this).inflate(R.layout.layout_maker_content, null);

        btnFindPath = (Button) findViewById(R.id.btn_findpath);
        etStartPos = (EditText) findViewById(R.id.et_startpos);
        etDestination = (EditText) findViewById(R.id.et_destination);
        btnInvite = (Button) contentView.findViewById(R.id.btnMarkerInvite);
        btnGetHere = (Button) contentView.findViewById(R.id.btn_GetHere);

        tvMarkerName = (TextView) contentView.findViewById(R.id.tvMarkerName);
        tvMarkerInfo1 = (TextView) contentView.findViewById(R.id.tvMarkerInfo1);
        tvMarkerInfo2 = (TextView) contentView.findViewById(R.id.tvMarkerInfo2);
        ivAvatar = (ImageView) contentView.findViewById(R.id.iv_avatar);
    }

    private void initListeners() {
        btnFindPath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendFindPathRequest();
            }
        });

        // Start of click button Invite
        lsClick = new OnInterInfoWindowTouchListener(btnInvite) {
            @Override
            protected void onClickConfirmed(View v, Marker marker) {
                /*int pos = Integer.parseInt(marker.getTitle());
                if (pos>=0) {
                    Intent smsIntent = new Intent(Intent.ACTION_VIEW);
                    smsIntent.setType("vnd.android-dir/mms-sms");
                    smsIntent.putExtra("sms_body", "Hi " + landmarks.get(pos).getName()
                            + ", I would like to invite you to have a meal with me at "
                            + eventName + ". Can you join me?");
                    smsIntent.putExtra("address", landmarks.get(pos).getPhoneNumber());

                    startActivity(smsIntent);
                }*/
                int pos = Integer.parseInt(marker.getTitle());

            }
        };
        btnInvite.setOnTouchListener(lsClick);
        // End of click button Invite

        //Start of click button GetHere
        ghClick = new OnInterInfoWindowTouchListener(btnGetHere) {
            @Override
            protected void onClickConfirmed(View v, Marker marker) {
                if (mMap.getMyLocation()!=null)
                    MyLoc = new LatLng(mMap.getMyLocation().getLatitude(), mMap.getMyLocation().getLongitude());
                etStartPos.setText(MyLoc.latitude + " " + MyLoc.longitude);
                etDestination.setText(marker.getPosition().latitude + " " + marker.getPosition().longitude);
                sendFindPathRequest();
            }
        };
        btnGetHere.setOnTouchListener(ghClick);
        //End of click button GetHere




    }

    private void sendFindPathRequest() {
        strStartPos= etStartPos.getText().toString();
        strDestination= etDestination.getText().toString();

        if (strStartPos.isEmpty())
        {
            Toast.makeText(this, "Please enter the starting point", Toast.LENGTH_SHORT).show();
            return;
        }

        if (strDestination.isEmpty())
        {
            Toast.makeText(this, "Please enter the destination", Toast.LENGTH_SHORT).show();
            return;
        }

        try{
            new DirectionFinder(this, strStartPos, strDestination).execute();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mapWrapperLayout.init(mMap, this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);

        //Init process


        //Marking process
        if (map_type==1) {
            eventPoint = new LatLng(pLandmark.getCoord().getLatitude(), pLandmark.getCoord().getLongitude());
            MarkPlace(-1);
        } else {
            loadPeopleData();
            nearbySearch();
        }


        //Handle Popup Info window
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                lsClick.setMarker(marker);
                ghClick.setMarker(marker);

                Integer pos = Integer.parseInt(marker.getTitle());
                if (pos==-1) {
                    tvMarkerName.setText("Name: " + pLandmark.getName());
                    tvMarkerInfo1.setText("Address: " );
                    tvMarkerInfo2.setVisibility(View.GONE);
                    ivAvatar.setVisibility(View.VISIBLE);
                    Picasso.with(MapsActivity.this)
                            .load(pLandmark.getImgUrl())
                            .resize(100,100)
                            .centerCrop()
                            .into(ivAvatar);
                    btnGetHere.setVisibility(View.VISIBLE);
                    btnInvite.setVisibility(View.GONE);
                } else {
                    tvMarkerInfo2.setVisibility(View.GONE);
                    ivAvatar.setVisibility(View.VISIBLE);
                    btnGetHere.setVisibility(View.VISIBLE);
                    btnInvite.setVisibility(View.GONE);

                    tvMarkerName.setText("Name: " + landmarks.get(pos).getName());
                    tvMarkerInfo1.setText("Rating: ★★★★★");
                    tvMarkerInfo2.setText("Email: ");
                    Picasso.with(MapsActivity.this)
                            .load(landmarks.get(pos).getImgUrl())
                            .resize(100,100)
                            .centerCrop()
                            .into(ivAvatar);

                }


                mapWrapperLayout.setMarkerWithInfoWindow(marker, contentView);
                return contentView;
            }
        });
    }


    private void MarkPlace(Integer id) {

        MarkerOptions mo2 = new MarkerOptions()
                .position(eventPoint)
                .title(id.toString())
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.blue_dot));

        MarkerOptions mo1 = new MarkerOptions()
                .position(eventPoint)
                .title(id.toString());
        if (id==-1)
            mMap.addMarker(mo1);
        else mMap.addMarker(mo2);

        mMap.animateCamera(CameraUpdateFactory.zoomTo(18), 2000, null);

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(eventPoint)
                .zoom(18)
                .bearing(90)
                .tilt(30)
                .build();

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    private void getData() {
        Intent intent = getIntent();
        map_type = intent.getIntExtra("map_type",1);
        if (map_type==1) {
            position = intent.getIntExtra("position", position);
            pLandmark = MainActivity.LandmarkList.get(position);
        }

    }


    @Override
    public void onDirectionFinderStart() {
        progressDialog = ProgressDialog.show(this, "Please wait", "Finding Direction", true);

        if (StartPosMarkers != null) {
            for (Marker marker : StartPosMarkers)
                marker.remove();
        }

        if (DestinationMarkers != null) {
            for (Marker marker : DestinationMarkers)
                marker.remove();
        }

        if (PolylinePaths != null) {
            for (Polyline polyline : PolylinePaths)
                polyline.remove();
        }
    }

    @Override
    public void onDirectionFinderSuccess(List<Route> routes) {
        progressDialog.dismiss();
        StartPosMarkers = new ArrayList<>();
        DestinationMarkers = new ArrayList<>();
        PolylinePaths = new ArrayList<>();

        for (Route route : routes) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 16));
            ((TextView) findViewById(R.id.tv_distance)).setText(route.distance.text);
            ((TextView) findViewById(R.id.tv_time)).setText(route.duration.text);

            StartPosMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_start))
                    .title(route.startAddress)
                    .position(route.startLocation)));

            DestinationMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_des))
                    .title(route.endAddress)
                    .position(route.endLocation)));

            PolylineOptions polylineOptions = new PolylineOptions()
                    .geodesic(true)
                    .color(Color.RED)
                    .width(15);

            for (int i = 0; i < route.points.size(); i++)
                polylineOptions.add(route.points.get(i));

            PolylinePaths.add(mMap.addPolyline(polylineOptions));
        }
    }



    /* This part is mainly for Inviting feature */
    private void loadPeopleData() {
        landmarks = MainActivity.LandmarkList;
    }

    private void nearbySearch() {
        //initMyLocation();
        String strOrigins = "";
        String strDestination = "";
        for (int i=0; i<landmarks.size(); i++) {
                strOrigins = strOrigins + landmarks.get(i).getCoord().getLatitude() + "," + landmarks.get(i).getCoord().getLongitude() + "|";
        }
        GPSTracker gps = new GPSTracker(this);
        strDestination = gps.getLatitude() + "," + gps.getLongitude();

        new GeoTask(MapsActivity.this).execute("http://maps.googleapis.com/maps/api/distancematrix/json?origins=" + strOrigins + "&destinations=" + strDestination);
    }

    @Override
    public void getResult(JSONObject jsonObject) {
        Log.i("JSON", jsonObject.toString());
        Dist = new ArrayList<>();
        try {
            JSONArray row = jsonObject.getJSONArray("rows");
            for (int i=0; i<row.length(); i++){
                JSONObject o = row.getJSONObject(i);
                JSONObject element = (JSONObject) o.getJSONArray("elements").get(0);
                Dist.add(element.getJSONObject("distance").getInt("value"));
                Log.d("Dist "+i+": ", String.valueOf(element.getJSONObject("distance").getInt("value")));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        for (int i=0; i<Dist.size(); i++){
            if (Dist.get(i)<=100000)
            {
                eventPoint = new LatLng(landmarks.get(i).getCoord().getLatitude(),landmarks.get(i).getCoord().getLongitude());
                MarkPlace(i);
            }
        }

        GPSTracker gps = new GPSTracker(this);
        eventPoint = new LatLng(gps.getLatitude(), gps.getLongitude());
        MarkPlace(-1);

    }

}
