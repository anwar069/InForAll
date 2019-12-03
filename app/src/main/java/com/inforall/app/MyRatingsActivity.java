package com.inforall.app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.inforall.app.rating.RatingAdapter;
import com.inforall.app.rating.RatingModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MyRatingsActivity extends AppCompatActivity {

    private DatabaseReference fbUserPlaceRef, fbCurrentUserRef;
    private FirebaseDatabase mFirebaseInstance;
    private RecyclerView RVratings;
    private TextView TVEmptyView;
    SharedPreferences pref;
    String LoginPrefFileName = "loginPref";
    Boolean logged_in = false;
    String loggedAccName = "";
    private ProgressDialog Dialog ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_ratings);

        initializeView();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fbUserPlaceRef = mFirebaseInstance.getReference("user_place_rating");
        fbCurrentUserRef = fbUserPlaceRef.child(loggedAccName.replace(".",""));

        fbCurrentUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<RatingModel> rateData=new ArrayList<RatingModel>();
                if(dataSnapshot.getValue()!=null)
                {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        rateData.add(new RatingModel(snapshot.child("name").getValue().toString(),snapshot.getKey().toString(),snapshot.child("reference").getValue().toString(),snapshot.child("rating").getValue().toString(),snapshot.child("distance").getValue().toString()));
                    }
                }


                if(rateData.size()==0){
                    RVratings.setVisibility(View.GONE);
                    TVEmptyView.setVisibility(View.VISIBLE);
                }else{
                    RVratings.setVisibility(View.VISIBLE);
                    TVEmptyView.setVisibility(View.GONE);
                    RatingAdapter rateAdapter=new RatingAdapter(getBaseContext(),rateData);
                    RVratings.setAdapter(rateAdapter);
                    RVratings.setLayoutManager(new LinearLayoutManager(MyRatingsActivity.this));
                }

                hideProgressDialog();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void hideProgressDialog() {
            Dialog.hide();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // todo: goto back activity from here

                Intent intent = new Intent(MyRatingsActivity.this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initializeView() {
        pref = getApplicationContext().getSharedPreferences(LoginPrefFileName, MODE_PRIVATE);
        logged_in = pref.getBoolean("IS_LOGIN", false);
        loggedAccName = pref.getString("LOGIN_ACCOUNT", "");

        if (!"".equals(loggedAccName)) {
            loggedAccName = loggedAccName.split("@")[0];
        }

        Dialog = new ProgressDialog(MyRatingsActivity.this);
        Dialog.setMessage("Loading...");
        Dialog.setCancelable(false);
        Dialog.setCanceledOnTouchOutside(false);
        Dialog.show();

        RVratings= (RecyclerView) findViewById(R.id.rvRatings);
        TVEmptyView= (TextView) findViewById(R.id.empty_view);
        mFirebaseInstance = FirebaseDatabase.getInstance();
    }

}
