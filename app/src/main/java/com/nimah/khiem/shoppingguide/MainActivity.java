package com.nimah.khiem.shoppingguide;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.widget.ProfilePictureView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    GridView gvLandmarkList;
    LandMarkListAdapter landMarkListAdapter = null;
    static ArrayList<Landmark> LandmarkList = new ArrayList<>();

    private UserProfile user;
    private ProfilePictureView ppv_Avatar;
    private TextView tv_UserName;
    private TextView tv_UserEmail;
    private Button btn_armap, btn_shownearby, btn_detect, btn_logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();



        loadLandmarkList();
        initComponents();
        initListeners();
        initFacebook();
    }

    private void initComponents() {
        gvLandmarkList = (GridView) findViewById(R.id.gv_landmark_list);
        landMarkListAdapter = new LandMarkListAdapter(this, R.layout.landmark_item_layout, LandmarkList);
        gvLandmarkList.setAdapter(landMarkListAdapter);

        /*NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.inflateHeaderView(R.layout.nav_header_main); */
        tv_UserName = (TextView) findViewById(R.id.tv_username);
        tv_UserEmail = (TextView) findViewById(R.id.tv_useremail);
        ppv_Avatar = (ProfilePictureView) findViewById(R.id.ppv_avatar);
        btn_armap = (Button) findViewById(R.id.btn_armap);
        btn_shownearby = (Button) findViewById(R.id.btn_shownearby);
        btn_detect = (Button) findViewById(R.id.btn_detect);
        btn_logout = (Button) findViewById(R.id.btn_logout);
    }

    private void initListeners() {
        btn_armap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ARDirection.class);
                startActivity(intent);
            }
        });

        btn_shownearby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                intent.putExtra("map_type", 2);
                startActivity(intent);
            }
        });

        btn_detect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Detect.class);
                startActivity(intent);
            }
        });

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logOut();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                finish();
                startActivity(intent);
            }
        });
    }

    private void loadLandmarkList() {
        MapCoord coord = new MapCoord(10.772493, 106.698078);
        Landmark landmark = new Landmark("Ben Thanh Market",
                "http://www.nghiaphat.vn/uploads/article/cho-ben-thanh-gia-tri-van-hoa_1407319290.jpg",
                "https://en.wikipedia.org/wiki/B%E1%BA%BFn_Th%C3%A0nh_Market",
                "https://www.youtube.com/watch?v=cQaX0U9y-7A",
                "http://www.ben-thanh-market.com/",
                coord);
        MapCoord coord1 = new MapCoord(10.781044, 106.698432);
        Landmark landmark1 = new Landmark("Diamond Plaza",
                "http://chothuevanphonghanga.com/wp-content/uploads/2015/07/Diamond-Plaza.jpg",
                "https://en.wikipedia.org/wiki/Diamond_Plaza",
                "https://www.youtube.com/watch?v=OVzuXZGS81U",
                "www.diamondplaza.com.vn/",
                coord1);
        MapCoord coord2 = new MapCoord(10.743367, 106.612873);
        Landmark landmark2 = new Landmark("AEON Mall Binh Tan",
                "http://vietnamfinance.vn/upload/news/thanhtam/2016/2/24/aeonmall.jpg",
                "https://en.wikipedia.org/wiki/AEON_(company)",
                "https://www.youtube.com/watch?v=Kl3PlrSRlH0",
                "www.aeonmall-binhtan.com.vn/",
                coord2);
        MapCoord coord3 = new MapCoord(10.728762, 106.718806);
        Landmark landmark3 = new Landmark("Crescent Mall",
                "https://www.arthitectural.com/wp-content/uploads/2012/08/DSC1702a.jpg",
                "https://vi.wikipedia.org",
                "https://www.youtube.com/watch?v=62MkQkJJ5OI",
                "crescentmall.com.vn/en/",
                coord3);
        MapCoord coord4 = new MapCoord(10.7296677, 106.7034895);
        Landmark landmark4 = new Landmark("SC VIVO City",
                "http://luxcity.land4vn.com/images/vivo.jpg",
                "https://vi.wikipedia.org",
                "https://www.youtube.com/watch?v=KnwFPMZUP0o",
                "scvivocity.com.vn/",
                coord4);
        MapCoord coord5 = new MapCoord(10.8226063, 106.6914583);
        Landmark landmark5 = new Landmark("Emart",
                "https://insideretail.asia/wp-content/uploads/2016/01/E-mart-Vietnam.png",
                "https://vi.wikipedia.org/wiki/E-mart",
                "https://www.youtube.com/watch?v=_CPD8ZTk3lY",
                "www.emart.com.vn/",
                coord5);
        LandmarkList.add(landmark);
        LandmarkList.add(landmark1);
        LandmarkList.add(landmark2);
        LandmarkList.add(landmark3);
        LandmarkList.add(landmark4);
        LandmarkList.add(landmark5);


    }


    /*--- Facebook Integration ---*/

    public boolean isLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }

    private void initFacebook() {
        user = new UserProfile();
        if (isLoggedIn() == true) {
            getUserProfile();
            getUserFriendList();
        }
    }

    private void getUserFriendList(){
        GraphRequest request = GraphRequest.newMyFriendsRequest(
                AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONArrayCallback() {
                    @Override
                    public void onCompleted(JSONArray objects, GraphResponse response) {
                        Log.d("fl", objects.toString());

                    }
                });
        request.executeAsync();
    }

    private void getUserProfile(){
        GraphRequest request = GraphRequest.newMeRequest(
                AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(final JSONObject object, GraphResponse response) {
                        Log.d("abc", object.toString());
                        try {
                            user.setFb_id(object.getString("id"));
                            user.setFb_name(object.getString("name"));
                            if (object.has("email")) {
                                user.setFb_email(object.getString("email"));
                            } else {
                                user.setFb_email("Enjoy your shopping!");
                            }
                            if (object.has("birthday")) {
                                user.setFb_birthday(object.getString("birthday"));
                            } else {
                                user.setFb_birthday("");
                            }
                            updateLayout();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
        );
        Bundle params = new Bundle();
        params.putString("fields", "id,name,email,birthday");
        request.setParameters(params);
        request.executeAsync();
    }

    private void updateLayout() {
        tv_UserName.setText(user.getFb_name());
        tv_UserEmail.setText(user.getFb_email());
        ppv_Avatar.setProfileId(user.getFb_id());
    }


    /*--- Option Menu & Navigation Bar ---*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_armap) {
            Intent intent = new Intent(MainActivity.this, ARDirection.class);
            startActivity(intent);
        } else if (id == R.id.nav_nearby) {
            Intent intent = new Intent(MainActivity.this, MapsActivity.class);
            intent.putExtra("map_type",2);
            startActivity(intent);

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_logout) {
            LoginManager.getInstance().logOut();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            finish();
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



    /*--- Other Settings ---*/

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

}
