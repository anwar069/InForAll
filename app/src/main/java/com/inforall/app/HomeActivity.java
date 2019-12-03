package com.inforall.app;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.inforall.app.place.PlaceAdapter;
import com.inforall.app.place.PlaceModel;
import com.inforall.app.places.Http;
import com.inforall.app.places.Places;
import com.inforall.app.utils.CustomArrayAdapter;

import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HomeActivity extends AppCompatActivity
        implements GoogleApiClient.ConnectionCallbacks, OnConnectionFailedListener, LocationListener, NavigationView.OnNavigationItemSelectedListener, SearchFragment.OnFragmentInteractionListener {

    private static final String SELECTED_ITEM = "arg_selected_item";
    private static final float DEFAULT_ZOOM = 17.0f;
    public static final String MAPS_API_PLACE_NEARBYSEARCH = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?rankBy=distance&";

    private BottomNavigationView mBottomNav;
    private int mSelectedItem;

    String[] PlaceType = {"accounting", "airport", "amusement_park", "aquarium", "art_gallery", "atm", "bakery", "bank", "bar", "beauty_salon", "bicycle_store", "book_store", "bowling_alley", "bus_station", "cafe", "campground", "car_dealer", "car_rental", "car_repair", "car_wash", "casino", "cemetery", "church", "city_hall", "clothing_store", "convenience_store", "courthouse", "dentist", "department_store", "doctor", "electrician", "electronics_store", "embassy", "establishment (deprecated)", "finance (deprecated)", "fire_station", "florist", "food (deprecated)", "funeral_home", "furniture_store", "gas_station", "general_contractor (deprecated)", "grocery_or_supermarket (deprecated)", "gym", "hair_care", "hardware_store", "health (deprecated)", "hindu_temple", "home_goods_store", "hospital", "insurance_agency", "jewelry_store", "laundry", "lawyer", "library", "liquor_store", "local_government_office", "locksmith", "lodging", "meal_delivery", "meal_takeaway", "mosque", "movie_rental", "movie_theater", "moving_company", "museum", "night_club", "painter", "park", "parking", "pet_store", "pharmacy", "physiotherapist", "place_of_worship (deprecated)", "plumber", "police", "post_office", "real_estate_agency", "restaurant", "roofing_contractor", "rv_park", "school", "shoe_store", "shopping_mall", "spa", "stadium", "storage", "store", "subway_station", "synagogue", "taxi_stand", "train_station", "transit_station", "travel_agency", "university", "veterinary_care", "zoo"};

    String LoginPrefFileName = "loginPref";
    SharedPreferences pref;

    ImageButton btnLogin, ibtnFind;
    LinearLayout llLoginInfo;
    NavigationView navigationView;
    ImageView ivUserImage;
    View header;
    Menu nav_Menu;
    int windowWidth = 0;
    boolean isError=true;

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    LocationManager mLocationManager;

    // BottomSheetBehavior variable
    private BottomSheetBehavior bottomSheetBehavior;


    MapView mMapView;
    private GoogleMap googleMap;
    private static final String GOOGLE_API_KEY = "AIzaSyABH6V1oWtQ0C7r5T7bXEKSkuJ1PS7ana4";

    EditText placeText;
    double latitude = 0;
    double longitude = 0;
    private int PROXIMITY_RADIUS = 5000;
    private int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 114;
    private boolean mLocationPermissionGranted = false;
    private Location mLastKnownLocation = null;

    private GoogleApiClient mGoogleApiClient;
    PlaceAdapter adapter;
    private LatLng mDefaultLocation = new LatLng(-34, 151);
    private CameraPosition mCameraPosition;
    AutoCompleteTextView actPlaceSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        initViews();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, LoginActivity.class));
            }
        });

        setLoginInfo();

        // Capturing the callbacks for bottom sheet
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(View bottomSheet, int newState) {

                // Check Logs to see how bottom sheets behaves
                switch (newState) {
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        Log.e("Bottom Sheet Behaviour", "STATE_COLLAPSED");
                        break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        Log.e("Bottom Sheet Behaviour", "STATE_DRAGGING");
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        Log.e("Bottom Sheet Behaviour", "STATE_EXPANDED");
                        break;
                    case BottomSheetBehavior.STATE_HIDDEN:
                        Log.e("Bottom Sheet Behaviour", "STATE_HIDDEN");
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        Log.e("Bottom Sheet Behaviour", "STATE_SETTLING");
                        break;
                }
            }


            @Override
            public void onSlide(View bottomSheet, float slideOffset) {

            }
        });

        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        mMapView = (MapView) findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

//        placeText = (EditText) findViewById(R.id.placeText);
//        Button btnFind = (Button) findViewById(R.id.btnFind);
        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(this.getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }


        ibtnFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    searchPlace();


            }
        });
    }

    private void searchPlace() {
        if(isError){
            AlertDialog alertDialog = new AlertDialog.Builder(HomeActivity.this).create();
            alertDialog.setTitle("Invalid place type!!");
            alertDialog.setMessage("Please start typing and select a place type form the list.");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
            return;
        }
        String type = actPlaceSearch.getText().toString();
        StringBuilder googlePlacesUrl = new StringBuilder(MAPS_API_PLACE_NEARBYSEARCH);
        googlePlacesUrl.append("location=" + mLastKnownLocation.getLatitude() + "," + mLastKnownLocation.getLongitude());
        googlePlacesUrl.append("&radius=" + PROXIMITY_RADIUS);
        googlePlacesUrl.append("&types=" + type);
        googlePlacesUrl.append("&sensor=true");
        googlePlacesUrl.append("&key=" + GOOGLE_API_KEY);

        GooglePlacesReadTask googlePlacesReadTask = new GooglePlacesReadTask();
        Object[] toPass = new Object[2];
        toPass[0] = googleMap;
        toPass[1] = googlePlacesUrl.toString();
        googlePlacesReadTask.execute(toPass);

        recyclerView = (RecyclerView) findViewById(R.id.place_recycler_view);

        layoutManager = new LinearLayoutManager(HomeActivity.this);
        recyclerView.setLayoutManager(layoutManager);

        // Check if no view has focus:
        View view = this.getCurrentFocus();
        recyclerView.setItemAnimator(new DefaultItemAnimator());  InputMethodManager imm = (InputMethodManager)getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

    }

    private void getDeviceLocation() {
    /*
     * Before getting the device location, you must check location
     * permission, as described earlier in the tutorial. Then:
     * Get the best and most recent location of the device, which may be
     * null in rare cases when a location is not available.
     */
        if (mLocationPermissionGranted) {
            mLastKnownLocation = LocationServices.FusedLocationApi
                    .getLastLocation(mGoogleApiClient);
        }

        LatLng current = new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
        // Set the map's camera position to the current location of the device.
        mCameraPosition = new CameraPosition.Builder().target(current).zoom(12).build();
        if (mCameraPosition != null) {
            googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(mCameraPosition));
        } else if (mLastKnownLocation != null) {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(mLastKnownLocation.getLatitude(),
                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
            googleMap.addMarker(new MarkerOptions().position(current).title("Marker Title").snippet("Marker Description"));
        } else {
            Log.d("Location: ", "Current location is null. Using defaults.");

            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
            googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        }
    }

    private void updateLocationUI() {
        if (googleMap == null) {
            return;
        }

    /*
     * Request location permission, so that we can get the location of the
     * device. The result of the permission request is handled by a callback,
     * onRequestPermissionsResult.
     */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }

        if (mLocationPermissionGranted) {
            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        } else {
            googleMap.setMyLocationEnabled(false);
            googleMap.getUiSettings().setMyLocationButtonEnabled(false);
            mLastKnownLocation = null;
        }
    }

    private void initViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        bottomSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.bottomSheetLayout));
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        header = navigationView.getHeaderView(0);
        nav_Menu = navigationView.getMenu();
        btnLogin = (ImageButton) header.findViewById(R.id.ibLogin);
        llLoginInfo = (LinearLayout) header.findViewById(R.id.llLoginInfo);
        ivUserImage = (ImageView) header.findViewById(R.id.ivLoginImg);
        actPlaceSearch = (AutoCompleteTextView) findViewById(R.id.actPlaceSearch);
        ibtnFind = (ImageButton) findViewById(R.id.ibtnFind);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        windowWidth = size.x;



        //Creating the instance of ArrayAdapter containing list of language names
        CustomArrayAdapter adapter = new CustomArrayAdapter(this, android.R.layout.select_dialog_item, new ArrayList<String>(Arrays.asList(PlaceType)));
        //Getting the instance of AutoCompleteTextView
        actPlaceSearch.setThreshold(1);//will start working from first character
        actPlaceSearch.setAdapter(adapter);//setting the adapter data into the AutoCompleteTextView
        actPlaceSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                actPlaceSearch.setError(null);
                isError=false;
//                ibtnFind.setEnabled(true);
//                ibtnFind.setClickable(true);
//
//                actPlaceSearch.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
//                actPlaceSearch.setImeActionLabel("Search",EditorInfo.IME_ACTION_SEARCH);
            }
        });
        actPlaceSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchPlace();
                    return true;
                }
                return false;
            }
        });
        actPlaceSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if (s.length() != 0) {
                    if (Arrays.asList(PlaceType).contains(s + "")) {
                        isError=false;
//                        ibtnFind.setEnabled(true);
//                        ibtnFind.setClickable(true);
//                        actPlaceSearch.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
//                        actPlaceSearch.setImeActionLabel("Search",EditorInfo.IME_ACTION_SEARCH);
                    } else {
                        actPlaceSearch.setError("Invalid Place Type");
                        isError=true;
//                        ibtnFind.setEnabled(false);
//                        ibtnFind.setClickable(false);
//                        actPlaceSearch.setImeOptions(EditorInfo.IME_ACTION_GO);
//                        actPlaceSearch.setImeActionLabel("Search",EditorInfo.IME_ACTION_NONE);
                    }
                }
            }
        });
    }

    @Override
    protected void onStart() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .enableAutoManage(this,
                        this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
        super.onStart();
    }

    private void setLoginInfo() {
        pref = getApplicationContext().getSharedPreferences(LoginPrefFileName, MODE_PRIVATE);
        Boolean logged_in = pref.getBoolean("IS_LOGIN", false);

        if (!logged_in) {
            btnLogin.setVisibility(View.VISIBLE);
            llLoginInfo.setVisibility(View.GONE);
            nav_Menu.findItem(R.id.account).setVisible(false);
            nav_Menu.findItem(R.id.nav_my_rating).setVisible(false);
        } else {
            btnLogin.setVisibility(View.GONE);
            llLoginInfo.setVisibility(View.VISIBLE);
            nav_Menu.findItem(R.id.account).setVisible(true);
            nav_Menu.findItem(R.id.nav_my_rating).setVisible(true);
            String name = pref.getString("LOGIN_NAME", "");
            String imgURL = pref.getString("LOGIN_URL", "");

            if (!"".equals(imgURL)) {
                Glide.with(getApplicationContext()).load(imgURL)
                        .asBitmap()
                        .centerCrop()
                        .into(new BitmapImageViewTarget(ivUserImage) {
                            @Override
                            protected void setResource(Bitmap resource) {
                                RoundedBitmapDrawable circularBitmapDrawable =
                                        RoundedBitmapDrawableFactory.create(getApplicationContext().getResources(), resource);
                                circularBitmapDrawable.setCircular(true);
                                ivUserImage.setImageDrawable(circularBitmapDrawable);
                            }
                        });
            }
            ((TextView) header.findViewById(R.id.loginName)).setText(name);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshSearchFragment();
    }

    private void refreshSearchFragment() {
//        Fragment frag = SearchFragment.newInstance("Search", "Result");
//        getSupportFragmentManager().beginTransaction().replace(R.id.container, frag).commit();
    }

    private void selectFragment(MenuItem item) {
        Fragment frag = null;
        // init corresponding fragment
//        switch (item.getItemId()) {
////            case R.id.action_call:
////                frag = CallFragment.newInstance("Call History", "Result");
////                break;
//            case R.id.action_search:
//                frag = SearchFragment.newInstance("Map", "Result");
//                break;
////            case R.id.action_contact:
////                frag = ContactFragment.newInstance("Contacts", "Result");
////                break;
//            case R.id.action_block:
//                frag = BlockFragment.newInstance("List", "Result");
//                break;
//
//        }
//
//        // update selected item
//        mSelectedItem = item.getItemId();
//
//        // uncheck the other items.
//        for (int i = 0; i < mBottomNav.getMenu().size(); i++) {
//            MenuItem menuItem = mBottomNav.getMenu().getItem(i);
//            menuItem.setChecked(menuItem.getItemId() == item.getItemId());
//        }

        updateToolbarText("In For All");

//        frag = SearchFragment.newInstance("Map", "Result");
//        if (frag != null) {
//            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//            ft.replace(R.id.container, frag, frag.getTag());
//            ft.commit();
//        }
    }

    private Boolean exit = false;

    @Override
    public void onBackPressed() {
        if (exit) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Press Back again to Exit.",
                    Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3 * 1000);

        }
    }

    private void updateToolbarText(CharSequence text) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(text);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.home, menu);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mGoogleApiClient.stopAutoManage(HomeActivity.this);
        mGoogleApiClient.disconnect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGoogleApiClient.stopAutoManage(HomeActivity.this);
        mGoogleApiClient.disconnect();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_help) {
            startActivity(new Intent(HomeActivity.this, HelpActivity.class));
        } else if (id == R.id.nav_my_rating) {
            startActivity(new Intent(HomeActivity.this, MyRatingsActivity.class));
        } else if (id == R.id.nav_share) {
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/html");
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, Html.fromHtml("<p> Please visit. <a>https://www.google.co.in/</a></p>"));
            startActivity(Intent.createChooser(sharingIntent, "Share using"));

//        } else if (id == R.id.nav_send) {
//            Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
//            whatsappIntent.setType("text/plain");
//            whatsappIntent.setPackage("com.whatsapp");
//            whatsappIntent.putExtra(Intent.EXTRA_TEXT, "Please visit. https://www.google.co.in/");
//            try {
//                startActivity(whatsappIntent);
//            } catch (android.content.ActivityNotFoundException ex) {
//                Toast.makeText(getApplicationContext(), "Whatsapp have not been installed.", Toast.LENGTH_SHORT).show();
//            }
        } else if (id == R.id.nav_setting) {
            //startActivity(new Intent(HomeActivity.this, SettingsActivity.class));
        } else if (id == R.id.logout) {
            revokeAccess();
        }
//        } else if (id == R.id.nav_sync) {
//            new AsyncContactsSync().execute();
//        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        LatLng latLng = new LatLng(latitude, longitude);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(DEFAULT_ZOOM));
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }


    private void showSuccesDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);

        // set title
        alertDialogBuilder.setTitle("Mobile Number Tracker");

        // set dialog message
        alertDialogBuilder
                .setMessage("Contacts synced successfully.")
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, close
                        // current activity
                        refreshSearchFragment();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    private void revokeAccess() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // ...
                        pref = getApplicationContext().getSharedPreferences(LoginPrefFileName, MODE_PRIVATE);
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putBoolean("IS_LOGIN", false);
                        editor.putString("LOGIN_NAME", "");
                        editor.putString("LOGIN_URL", "");
                        editor.putString("LOGIN_ACCOUNT", "");
                        editor.commit();
                        Toast.makeText(getApplicationContext(), "Logged Out", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(i);
                    }
                });
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        Toast.makeText(this, "Changed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("Google API Build", "Play services connection failed: ConnectionResult.getErrorCode() = "
                + connectionResult.getErrorCode());
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;

                updateLocationUI();

                // Get the current location of the device and set the position of the map.
                getDeviceLocation();

            }
        });
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d("Google API Build", "Play services connection suspended");
    }

    public class GooglePlacesReadTask extends AsyncTask<Object, Integer, String> {
        String googlePlacesData = null;
        GoogleMap googleMap;

        @Override
        protected String doInBackground(Object... inputObj) {
            try {
                googleMap = (GoogleMap) inputObj[0];
                String googlePlacesUrl = (String) inputObj[1];
                Http http = new Http();
                googlePlacesData = http.read(googlePlacesUrl);
            } catch (Exception e) {
                Log.d("Google Place Read Task", e.toString());
            }
            return googlePlacesData;
        }

        @Override
        protected void onPostExecute(String result) {
            PlacesDisplayTask placesDisplayTask = new PlacesDisplayTask();
            Object[] toPass = new Object[2];
            toPass[0] = googleMap;
            toPass[1] = result;
            placesDisplayTask.execute(toPass);
        }
    }

    public class PlacesDisplayTask extends AsyncTask<Object, Integer, List<PlaceModel>> {

        JSONObject googlePlacesJson;
        GoogleMap googleMap;

        @Override
        protected List<PlaceModel> doInBackground(Object... inputObj) {

            List<PlaceModel> googlePlacesList = null;
            Places placeJsonParser = new Places();

            try {
                googleMap = (GoogleMap) inputObj[0];
                googlePlacesJson = new JSONObject((String) inputObj[1]);
                googlePlacesList = placeJsonParser.parse(googlePlacesJson);
            } catch (Exception e) {
                Log.d("Exception", e.toString());
            }
            return googlePlacesList;
        }

        @Override
        protected void onPostExecute(final List<PlaceModel> list) {
            googleMap.clear();
            LatLngBounds.Builder builder = new LatLngBounds.Builder();

            for (int i = 0; i < list.size(); i++) {
                if(list.get(i).getPlace_id()==null)
                    continue;
                MarkerOptions markerOptions = new MarkerOptions();
                PlaceModel googlePlace = list.get(i);
                double lat = googlePlace.getLat();
                double lng = googlePlace.getLng();
                String placeName = googlePlace.getName();
                String vicinity = googlePlace.getVicinity();
                LatLng latLng = new LatLng(lat, lng);
                markerOptions.position(latLng);
//                markerOptions.title(placeName + " : " + vicinity);
                markerOptions.title(i + "");
                googleMap.addMarker(markerOptions);

                builder.include(markerOptions.getPosition());
            }

            googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    Intent contactIntent = new Intent(getBaseContext(), PlaceActivity.class);
                    final PlaceModel myPlace = list.get(Integer.parseInt(marker.getTitle()));
                    contactIntent.putExtra("Name", myPlace.getName());
                    contactIntent.putExtra("Id", myPlace.getPlace_id());
                    contactIntent.putExtra("reference", myPlace.getReference());
                    Location loc = new Location("");
                    loc.setLatitude(myPlace.getLat());
                    loc.setLongitude(myPlace.getLng());
                    contactIntent.putExtra("distance", String.format("%.2f", mLastKnownLocation.distanceTo(loc) / 1000) + " km.");
                    startActivity(contactIntent);
                }
            });

            // Setting a custom info window adapter for the google map
            googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

                // Use default InfoWindow frame
                @Override
                public View getInfoWindow(Marker arg0) {
                    return null;
                }

                // Defines the contents of the InfoWindow
                @Override
                public View getInfoContents(Marker marker) {
                    TextView textName;
                    TextView textAddress;
                    TextView textDistance;
                    TextView textRating;
                    RatingBar rbRating;
                    ImageView imgNavigate;

                    // Getting view from the layout file info_window_layout
                    View v = getLayoutInflater().inflate(R.layout.card_place, null);
                    final PlaceModel myPlace = list.get(Integer.parseInt(marker.getTitle()));

                    // Getting the position from the marker
//                    LatLng latLng = arg0.getPosition();


//                    Toast.makeText(getApplicationContext(),windowWidth+"",Toast.LENGTH_SHORT).show();
                    // Set desired height and width
                    v.setLayoutParams(new LinearLayout.LayoutParams(windowWidth, LinearLayout.LayoutParams.WRAP_CONTENT));


                    textName = (TextView) v.findViewById(R.id.tvName);
                    textAddress = (TextView) v.findViewById(R.id.tvAddress);
                    textDistance = (TextView) v.findViewById(R.id.tvDistance);
                    textRating = (TextView) v.findViewById(R.id.tvRating);
                    rbRating = (RatingBar) v.findViewById(R.id.rbrating);
                    imgNavigate = (ImageView) v.findViewById(R.id.iv_route);
//            textLocation = (TextView) v.findViewById(R.id.tvSearchAddress);
//            textOther = (TextView) v.findViewById(R.id.tvSearchPrice);

                    Location loc = new Location("");
                    loc.setLatitude(myPlace.getLat());
                    loc.setLongitude(myPlace.getLng());

                    textName.setText(myPlace.getName());
                    textAddress.setText(myPlace.getVicinity());
                    textDistance.setText(String.format("%.2f", mLastKnownLocation.distanceTo(loc) / 1000) + " km.");
                    textRating.setText(myPlace.getRating());
                    if (!"".equals(myPlace.getRating()) && myPlace.getRating() != null)
                        rbRating.setRating(Float.parseFloat(myPlace.getRating()));

                    imgNavigate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String geoUri = "http://maps.google.com/maps?q=loc:" + myPlace.getLat() + "," + myPlace.getLng() + " (" + myPlace.getName() + ")";
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    });

//                    // Getting reference to the TextView to set latitude
//                    TextView tvLat = (TextView) v.findViewById(R.id.tv_lat);
//
//                    // Getting reference to the TextView to set longitude
//                    TextView tvLng = (TextView) v.findViewById(R.id.tv_lng);
//
//                    // Setting the latitude
//                    tvLat.setText("Latitude:" + latLng.latitude);
//
//                    // Setting the longitude
//                    tvLng.setText("Longitude:"+ latLng.longitude);

                    // Returning the view containing InfoWindow contents
                    return v;

                }
            });

            builder.include(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()));
            LatLngBounds bounds = builder.build();
            int padding = 50; // offset from edges of the map in pixels
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
            googleMap.animateCamera(cu);


            /*adapter = new PlaceAdapter(getBaseContext(),list,mLastKnownLocation);
            recyclerView.setAdapter(adapter);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);*/
        }
    }
}
