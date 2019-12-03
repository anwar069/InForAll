package com.inforall.app.places;

import com.inforall.app.place.PlaceModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Places {

    public List<PlaceModel> parse(JSONObject jsonObject) {
        JSONArray jsonArray = null;
        try {
            jsonArray = jsonObject.getJSONArray("results");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return getPlaces(jsonArray);
    }

    private List<PlaceModel> getPlaces(JSONArray jsonArray) {
        int placesCount = jsonArray.length();
        List<PlaceModel> placesList = new ArrayList<PlaceModel>();


        for (int i = 0; i < placesCount; i++) {
            try {
                placesList.add(getPlace((JSONObject) jsonArray.get(i)));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return placesList;
    }

    private PlaceModel getPlace(JSONObject googlePlaceJson) {
        PlaceModel place=new PlaceModel();

        try {
            if (!googlePlaceJson.isNull("name")) {
                place.setName(googlePlaceJson.getString("name"));
            }
            if (!googlePlaceJson.isNull("vicinity")) {
                place.setVicinity(googlePlaceJson.getString("vicinity"));
            }
            place.setRating(googlePlaceJson.getString("rating"));
            place.setPlace_id(googlePlaceJson.getString("place_id"));
            place.setLat (Double.parseDouble(googlePlaceJson.getJSONObject("geometry").getJSONObject("location").getString("lat")));
            place.setLng (Double.parseDouble(googlePlaceJson.getJSONObject("geometry").getJSONObject("location").getString("lng")));
            place.setReference(googlePlaceJson.getString("reference"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return place;
    }
}