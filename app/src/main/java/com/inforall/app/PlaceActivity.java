package com.inforall.app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.inforall.app.places.PlaceDetailsJSONParser;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static android.Manifest.permission.CALL_PHONE;


public class PlaceActivity extends AppCompatActivity {
    private static final int PERMISSIONS_REQUEST_CALL_PHONE = 101;
    ImageButton ibUserPic;
    String LoginPrefFileName = "loginPref";

    String name, placeID, reference, distance,photo_reference;

    float googleRating, appRating;

    boolean appRatingFetched = false, googleRatingFetched = false,ratingSet=false,statusFetched=false;

    public static final String MAPS_API_PLACE_PHOTO= "https://maps.googleapis.com/maps/api/place/photo?maxwidth=800&photoreference=";
    private static final String GOOGLE_API_KEY = "AIzaSyABH6V1oWtQ0C7r5T7bXEKSkuJ1PS7ana4";
    // Progress dialog
    ProgressDialog pDialog;

    TextView tvName, tvAddress, tvRating, tvDistance,tvMyRating;

    ImageView ivPlaceIcon,ivHeaderImage;
    Button btnCall, btnRatePlace, btnRateLogin;

    Boolean placeExistInDB = false;
    RatingBar rbPlaceRating;
    SharedPreferences pref;

    Boolean logged_in = false;
    String loggedAccName = "";

    private DatabaseReference fbPlaceRatingRef, fbCurrentPlaceRef, fbUserPlaceRef, fbCurrentUserRef, fbCurrentRatingRef;
    private FirebaseDatabase mFirebaseInstance;
    private ProgressDialog Dialog ;

    // KEY Strings
    public static String API_KEY = "AIzaSyABH6V1oWtQ0C7r5T7bXEKSkuJ1PS7ana4";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place);

        initializeView();

        name = getIntent().getStringExtra("Name");
        placeID = getIntent().getStringExtra("Id");
        reference = getIntent().getStringExtra("reference");
        distance = getIntent().getStringExtra("distance");
        photo_reference=getIntent().getStringExtra("photo_reference");

        StringBuilder sb = new StringBuilder("https://maps.googleapis.com/maps/api/place/details/json?");
        sb.append("reference=" + reference);
        sb.append("&sensor=true");
        sb.append("&key=" + API_KEY);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Creating a new non-ui thread task to download Google place details
        final PlacesTask placesTask = new PlacesTask();

        // Invokes the "doInBackground()" method of the class PlaceTask
        placesTask.execute(sb.toString());


        fbPlaceRatingRef = mFirebaseInstance.getReference("place_rating");
        fbCurrentPlaceRef = fbPlaceRatingRef.child(placeID);
        fbUserPlaceRef = mFirebaseInstance.getReference("user_place_rating");
        fbCurrentUserRef = fbUserPlaceRef.child(loggedAccName.replace(".",""));
        fbCurrentRatingRef = fbCurrentUserRef.child(placeID);

        fbCurrentRatingRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()!=null)
                {
                    btnRatePlace.setVisibility(View.GONE);
                    tvMyRating.setVisibility(View.VISIBLE);
                    rbPlaceRating.setRating(Float.parseFloat(dataSnapshot.child("rating").getValue().toString()));
                    rbPlaceRating.setIsIndicator(true);
                }
                statusFetched=true;
                hideProgressDialog();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        fbCurrentPlaceRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                appRatingFetched = true;
                if (dataSnapshot.getValue() != null)
                    appRating = Float.parseFloat(dataSnapshot.child("rating").getValue().toString());
                else
                    appRating = -1;
                setRating();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        fbCurrentPlaceRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    placeExistInDB = true;
//                    Toast.makeText(getBaseContext(), "Child Mubarak", Toast.LENGTH_LONG).show();
                } else {
                    placeExistInDB = false;
//                    Toast.makeText(getBaseContext(), "Sorry :-(", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

       /*
        fbPlaceRatingRef.setValue("Realtime Database");
        String userId = fbPlaceRatingRef.push().getKey();

        Toast.makeText(this,"User Id: "+userId,Toast.LENGTH_LONG).show();*/

        btnRateLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PlaceActivity.this, LoginActivity.class));
            }
        });

        btnRatePlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (placeExistInDB) {

                    fbCurrentPlaceRef.runTransaction(new Transaction.Handler() {
                        @Override
                        public Transaction.Result doTransaction(MutableData mutableData) {
                            if (mutableData == null)
                                return Transaction.success(mutableData);
                            else {
                                Map<String, String> userData = new HashMap<String, String>();
                                userData.put("reference", mutableData.child("reference").getValue() + "");
                                userData.put("name", mutableData.child("name").getValue() + "");

                                String count = mutableData.child("count").getValue().toString();
                                int newCount = Integer.parseInt(count) + 1;
                                String rating = mutableData.child("rating").getValue().toString();
                                float oldRating = Float.parseFloat(rating);
                                float newRating = (oldRating + rbPlaceRating.getRating()) / 2;
                                userData.put("rating", newRating + "");
                                userData.put("count", newCount + "");

                                mutableData.setValue(userData);
                                return Transaction.success(mutableData);
                            }
                        }

                        @Override
                        public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

                        }
                    });
                } else {
                    Map<String, String> userData = new HashMap<String, String>();
                    userData.put("reference", reference);
                    userData.put("rating", rbPlaceRating.getRating() + "");
                    userData.put("count", "1");
                    userData.put("name", name);
                    fbCurrentPlaceRef.setValue(userData);
                }
                Map<String, String> ratingData = new HashMap<String, String>();
                ratingData.put("rating", rbPlaceRating.getRating() + "");
                ratingData.put("name", name);
                ratingData.put("reference", reference);
                ratingData.put("distance",distance);
                fbCurrentRatingRef.setValue(ratingData);
            }
        });
    }

    private void hideProgressDialog() {
        if(ratingSet&&statusFetched)
            Dialog.hide();
    }

    private void setRating() {
        if (appRatingFetched && googleRatingFetched) {
            float finalRating;
            if (appRating == -1)
                finalRating = googleRating;
            else
                finalRating = (googleRating + appRating) / 2;
            tvRating.setText(String.format("%.1f", finalRating));
            ratingSet=true;
            hideProgressDialog();
        }
    }

    public void CallMobile(String number) {
        int permissionCheck = ContextCompat.checkSelfPermission(this, CALL_PHONE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{CALL_PHONE},
                    PERMISSIONS_REQUEST_CALL_PHONE);
        } else {
            startActivity(new Intent(Intent.ACTION_CALL).setData(Uri.parse("tel:" + number)));
        }
    }

    private void initializeView() {
        tvName = (TextView) findViewById(R.id.tv_place_name);
        tvAddress = (TextView) findViewById(R.id.tv_place_addr);
        ivPlaceIcon = (ImageView) findViewById(R.id.iv_place_icon);
        tvRating = (TextView) findViewById(R.id.tv_place_rate);
        tvDistance = (TextView) findViewById(R.id.tv_place_dist);
        btnCall = (Button) findViewById(R.id.btnCall);
        btnRatePlace = (Button) findViewById(R.id.btn_rate_place);
        btnRateLogin = (Button) findViewById(R.id.btn_login_rate);
        rbPlaceRating = (RatingBar) findViewById(R.id.rb_place_rating);
        tvMyRating= (TextView) findViewById(R.id.tvMyRating);
        ivHeaderImage= (ImageView) findViewById(R.id.header_cover_image);

        pref = getApplicationContext().getSharedPreferences(LoginPrefFileName, MODE_PRIVATE);
        logged_in = pref.getBoolean("IS_LOGIN", false);
        loggedAccName = pref.getString("LOGIN_ACCOUNT", "");

        if (!"".equals(loggedAccName)) {
            loggedAccName = loggedAccName.split("@")[0];
        }

        if (logged_in) {
            btnRateLogin.setVisibility(View.GONE);
            btnRatePlace.setVisibility(View.VISIBLE);
        } else {
            btnRateLogin.setVisibility(View.VISIBLE);
            btnRatePlace.setVisibility(View.GONE);
        }

        Dialog = new ProgressDialog(PlaceActivity.this);
        Dialog.setMessage("Loading...");
        Dialog.setCancelable(false);
        Dialog.setCanceledOnTouchOutside(false);
        Dialog.show();


        mFirebaseInstance = FirebaseDatabase.getInstance();
    }


    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);


            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();
            br.close();

        } catch (Exception e) {
            Log.d("Exception Download", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }

        return data;
    }


    /**
     * A class, to download Google Place Details
     */
    private class PlacesTask extends AsyncTask<String, Integer, String> {

        String data = null;

        // Invoked by execute() method of this object
        @Override
        protected String doInBackground(String... url) {
            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        // Executed after the complete execution of doInBackground() method
        @Override
        protected void onPostExecute(String result) {
            ParserTask parserTask = new ParserTask();

            // Start parsing the Google place details in JSON format
            // Invokes the "doInBackground()" method of the class ParseTask
            parserTask.execute(result);
        }
    }


    /**
     * A class to parse the Google Place Details in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, HashMap<String, String>> {

        JSONObject jObject;

        // Invoked by execute() method of this object
        @Override
        protected HashMap<String, String> doInBackground(String... jsonData) {

            HashMap<String, String> hPlaceDetails = null;
            PlaceDetailsJSONParser placeDetailsJsonParser = new PlaceDetailsJSONParser();

            try {
                jObject = new JSONObject(jsonData[0]);

                // Start parsing Google place details in JSON format
                hPlaceDetails = placeDetailsJsonParser.parse(jObject);

            } catch (Exception e) {
                Log.d("Exception", e.toString());
            }
            return hPlaceDetails;
        }

        // Executed after the complete execution of doInBackground() method
        @Override
        protected void onPostExecute(HashMap<String, String> hPlaceDetails) {


            final String name = hPlaceDetails.get("name");
            String icon = hPlaceDetails.get("icon");
            String vicinity = hPlaceDetails.get("vicinity");
            final String lat = hPlaceDetails.get("lat");
            final String lng = hPlaceDetails.get("lng");
            String formatted_address = hPlaceDetails.get("formatted_address");
            final String formatted_phone = hPlaceDetails.get("formatted_phone");
            String website = hPlaceDetails.get("website");
            String international_phone_number = hPlaceDetails.get("international_phone_number");
            String url = hPlaceDetails.get("url");
            String photo_reference= hPlaceDetails.get("photo_reference");



            if(!"".equals(photo_reference)&&photo_reference!=null){
                String imgURL= MAPS_API_PLACE_PHOTO+photo_reference+"&key="+GOOGLE_API_KEY;
                Glide.with(getApplicationContext()).load(imgURL)
                        .asBitmap()
                        .centerCrop()
                        .into(ivHeaderImage);

            }

            googleRatingFetched = true;
            googleRating = Float.parseFloat(hPlaceDetails.get("rating"));
            setRating();

            tvName.setText(name);
            tvAddress.setText(formatted_address);
            tvDistance.setText(distance);

           /* ((LinearLayout)findViewById(R.id.ll_direction)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    StartNavigation(lat, lng, name);
                }
            });
            ((TextView)findViewById(R.id.tv_direction)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    StartNavigation(lat, lng, name);
                }
            });*/
            ((Button) findViewById(R.id.btnDirection)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    StartNavigation(lat, lng, name);
                }
            });

            if ("".equals(formatted_phone)) {
                btnCall.setVisibility(View.GONE);
            }

            btnCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CallMobile(formatted_phone);
                }
            });

            if (!"".equals(icon)) {
                Glide.with(getApplicationContext()).load(icon)
                        .asBitmap()
                        .centerCrop()
                        .into(new BitmapImageViewTarget(ivPlaceIcon) {
                            @Override
                            protected void setResource(Bitmap resource) {
                                RoundedBitmapDrawable circularBitmapDrawable =
                                        RoundedBitmapDrawableFactory.create(getApplicationContext().getResources(), resource);
                                circularBitmapDrawable.setCircular(true);
                                ivPlaceIcon.setImageDrawable(circularBitmapDrawable);
                            }
                        });
            }


//            String mimeType = "text/html";
//            String encoding = "utf-8";
//
//            String data = 	"<html>"+
//                    "<body><img style='float:left' src="+icon+" /><h1><center>"+name+"</center></h1>" +
//                    "<br style='clear:both' />" +
//                    "<hr  />"+
//                    "<p>Vicinity : " + vicinity + "</p>" +
//                    "<p>Location : " + lat + "," + lng + "</p>" +
//                    "<p>Address : " + formatted_address + "</p>" +
//                    "<p>Phone : " + formatted_phone + "</p>" +
//                    "<p>Website : " + website + "</p>" +
//                    "<p>Rating : " + rating + "</p>" +
//                    "<p>International Phone  : " + international_phone_number + "</p>" +
//                    "<p>URL  : <a href='" + url + "'>" + url + "</p>" +
//                    "</body></html>";
//
//            // Setting the data in WebView
//            mWvPlaceDetails.loadDataWithBaseURL("", data, mimeType, encoding, "");
        }
    }

    private void StartNavigation(String lat, String lng, String name) {
        String geoUri = "http://maps.google.com/maps?q=loc:" + lat + "," + lng + " (" + name + ")";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public static Drawable LoadImageFromWebOperations(String url) {
        try {
            InputStream is = (InputStream) new URL(url).getContent();
            Drawable d = Drawable.createFromStream(is, "src name");
            return d;
        } catch (Exception e) {
            return null;
        }
    }


    public static Drawable drawableFromUrl(String url) throws IOException {
        Bitmap x;

        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.connect();
        InputStream input = connection.getInputStream();

        x = BitmapFactory.decodeStream(input);
        return new BitmapDrawable(x);
    }


}
