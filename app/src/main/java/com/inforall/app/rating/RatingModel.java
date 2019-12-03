package com.inforall.app.rating;

import com.inforall.app.place.PlaceModel;

/**
 * Created by Ahmed on 19/09/2017.
 */

public class RatingModel {
    String PlaceName;
    String Id;
    String Reference;
    String Rating;
    String Distance;

    public String getDistance() {
        return Distance;
    }

    public void setDistance(String distance) {
        Distance = distance;
    }


    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }


    public String getPlaceName() {
        return PlaceName;
    }

    public void setPlaceName(String placeName) {
        PlaceName = placeName;
    }

    public String getReference() {
        return Reference;
    }

    public void setReference(String reference) {
        Reference = reference;
    }

    public String getRating() {
        return Rating;
    }

    public void setRating(String rating) {
        Rating = rating;
    }

    public RatingModel(String placeName, String placeId, String reference, String rating, String distance) {
        Id = placeId;
        PlaceName = placeName;
        Reference = reference;
        Rating = rating;
        Distance=distance;
    }
}
