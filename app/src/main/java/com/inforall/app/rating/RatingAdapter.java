package com.inforall.app.rating;
/**
 * Created by Ahmed on 12/01/2017.
 */


import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.inforall.app.PlaceActivity;
import com.inforall.app.R;
import com.inforall.app.place.PlaceModel;

import java.util.Collections;
import java.util.List;

public class RatingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    List<RatingModel> data = Collections.emptyList();
    RatingModel current;
    int currentPos = 0;

    // create constructor to initialize context and data sent from MainActivity
    public RatingAdapter(Context context, List<RatingModel> data) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = data;
    }

    // Inflate the layout when ViewHolder created
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.card_rating, parent, false);
        MyHolder holder = new MyHolder(view);
        return holder;
    }

    // Bind data
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        // Get current position of item in RecyclerView to bind data and assign values from list
        MyHolder myHolder = (MyHolder) holder;
        RatingModel current = data.get(position);

        myHolder.textName.setText(current.getPlaceName());
        myHolder.textRating.setText(current.getRating());
        myHolder.rbRating.setRating(Float.parseFloat(current.getRating()));
    }


    // return total item from List
    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView textName;
        TextView textRating;
        RatingBar rbRating;
        ImageView imgNavigate;
//        TextView textOther;

        // create constructor to get widget reference
        public MyHolder(View itemView) {
            super(itemView);
            textName = (TextView) itemView.findViewById(R.id.tvName);
            textRating = (TextView) itemView.findViewById(R.id.tvRating);
            rbRating = (RatingBar) itemView.findViewById(R.id.rbrating);
            imgNavigate = (ImageView) itemView.findViewById(R.id.iv_route);
//            textLocation = (TextView) itemView.findViewById(R.id.tvSearchAddress);
//            textOther = (TextView) itemView.findViewById(R.id.tvSearchPrice);
            itemView.setOnClickListener(this);

            imgNavigate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    RatingModel current = data.get(MyHolder.this.getLayoutPosition());
                    Intent contactIntent = new Intent(context, PlaceActivity.class);
                    contactIntent.putExtra("Name", current.getPlaceName());
                    contactIntent.putExtra("Id", current.getId());
                    contactIntent.putExtra("reference", current.getReference());
                    contactIntent.putExtra("distance", current.getDistance());
                    context.startActivity(contactIntent);
                }
            });
        }


        // Click event for all items
        @Override
        public void onClick(View v) {
            Intent contactIntent = new Intent(context, PlaceActivity.class);
        }
    }
}
