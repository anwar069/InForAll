package com.inforall.app.place;
/**
 * Created by Ahmed on 12/01/2017.
 */


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.inforall.app.ContactActivity;
import com.inforall.app.PlaceActivity;
import com.inforall.app.R;

import java.util.Collections;
import java.util.List;

public class PlaceAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    List<PlaceModel> data = Collections.emptyList();
    public static String prefFilename = "ContactPreferences";
    SharedPreferences preferences;
    PlaceModel current;
    int currentPos = 0;
    Location currentLocation;

    // create constructor to initialize context and data sent from MainActivity
    public PlaceAdapter(Context context, List<PlaceModel> data, Location loc) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.currentLocation = loc;
        this.data = data;
    }

    // Inflate the layout when ViewHolder created
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.card_place, parent, false);
        MyHolder holder = new MyHolder(view);
        return holder;
    }

    // Bind data
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        // Get current position of item in RecyclerView to bind data and assign values from list
        MyHolder myHolder = (MyHolder) holder;
        PlaceModel current = data.get(position);
        myHolder.textName.setText(current.getName());

        myHolder.textRating.setText(current.getRating());
        myHolder.textAddress.setText(current.getVicinity());
        if (!"".equals(current.getRating()) && current.getRating()!=null)
            myHolder.rbRating.setRating(Float.parseFloat(current.getRating()));

        Location loc = new Location("");
        loc.setLatitude(current.getLat());
        loc.setLongitude(current.getLng());
        myHolder.textDistance.setText(String.format("%.2f", currentLocation.distanceTo(loc) / 1000) + " km.");
//        if(!"".equals(current.getAddress().trim()))
//            myHolder.textLocation.setText(current.getAddress());
//        else
//            myHolder.textLocation.setText("Location not available.");

//        myHolder.textOther.setText("Rs. " + current.getOther()+ "\\Kg");
//        myHolder.textName.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
    }

    private void saveContactToPref(PlaceModel sModel) {
        preferences = context.getSharedPreferences(prefFilename, 0);

        String jsonContactStr = preferences.getString("CONTACTJSON", "{}");

//        JSONObject jsonContactObj = null;
//        try {
//            jsonContactObj = new JSONObject(jsonContactStr);
//            JSONObject newProfObj = new JSONObject();
//            newProfObj.put("FirstName", sModel.getFirstName());
//            newProfObj.put("LastName", sModel.getLastName());
//            newProfObj.put("Address", sModel.getAddress());
//            newProfObj.put("Email", sModel.getEmail());
//            newProfObj.put("Mobile", sModel.getMobile());
//            newProfObj.put("PicURL", sModel.getPicUrl());
//            jsonContactObj.put(sModel.getMobile(), newProfObj);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

//        SharedPreferences.Editor editor = preferences.edit();
//        editor.putString("CONTACTJSON", jsonContactObj.toString());
//        editor.commit();
    }

    // return total item from List
    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView textName;
        TextView textAddress;
        TextView textDistance;
        TextView textRating;
        RatingBar rbRating;
        ImageView imgNavigate;
//        TextView textOther;

        // create constructor to get widget reference
        public MyHolder(View itemView) {
            super(itemView);
            textName = (TextView) itemView.findViewById(R.id.tvName);
            textAddress = (TextView) itemView.findViewById(R.id.tvAddress);
            textDistance = (TextView) itemView.findViewById(R.id.tvDistance);
            textRating = (TextView) itemView.findViewById(R.id.tvRating);
            rbRating = (RatingBar) itemView.findViewById(R.id.rbrating);
            imgNavigate = (ImageView) itemView.findViewById(R.id.iv_route);
//            textLocation = (TextView) itemView.findViewById(R.id.tvSearchAddress);
//            textOther = (TextView) itemView.findViewById(R.id.tvSearchPrice);
            itemView.setOnClickListener(this);

            imgNavigate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PlaceModel current = data.get(MyHolder.this.getLayoutPosition());
                    String geoUri = "http://maps.google.com/maps?q=loc:" + current.getLat() + "," + current.getLng() + " (" + current.getName() + ")";
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            });
        }


        // Click event for all items
        @Override
        public void onClick(View v) {
            Intent contactIntent = new Intent(context, PlaceActivity.class);
            PlaceModel current = data.get(this.getLayoutPosition());
//            Toast.makeText(context, current.getMobile(), Toast.LENGTH_SHORT).show();
//            saveContactToPref(current);
           /* contactIntent.putExtra("Name", current.getName());
            contactIntent.putExtra("Number", );
            contactIntent.putExtra("Location", textLocation.getText());
            contactIntent.putExtra("Email",("".equals(current.getEmail().trim()))?"Email not available":current.getEmail());
            contactIntent.putExtra("ImageURL",current.getPicUrl());
            context.startActivity(contactIntent);*/
        }
    }
}
