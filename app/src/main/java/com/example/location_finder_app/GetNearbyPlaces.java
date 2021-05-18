package com.example.location_finder_app;

import android.os.AsyncTask;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class GetNearbyPlaces extends AsyncTask<Object, String, String>
{
    private String googleplaceData;
    private GoogleMap mMap;

    @Override
    protected String doInBackground(Object... objects){
        mMap = (GoogleMap) objects[0];
        String url = (String) objects[1];
        DownloadURL downloadURL = new DownloadURL();
        try {
            googleplaceData = downloadURL.ReadTheURL(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return googleplaceData;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onPostExecute(String s){
        List<HashMap<String,String>>nearByPlacesList = null;
        DataParser dataParser = new DataParser();
        try {
            nearByPlacesList = dataParser.parse(s);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        assert nearByPlacesList != null;
        DisplayNearbyPlaces(nearByPlacesList);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void DisplayNearbyPlaces(List<HashMap<String,String>>nearByPlacesList)
    {
        for (int i=0;i<nearByPlacesList.size();i++){
            MarkerOptions markerOptions = new MarkerOptions();
            HashMap<String,String>googleNearbyPlaces = nearByPlacesList.get(i);
            String nameOfPlaces = googleNearbyPlaces.get("place_name");
            String vicinity = googleNearbyPlaces.get("vicinity");
            double lat = Double.parseDouble(Objects.requireNonNull(googleNearbyPlaces.get("lat")));
            double lng = Double.parseDouble(Objects.requireNonNull(googleNearbyPlaces.get("lng")));

            LatLng latLng = new LatLng(lat, lng);
            markerOptions.position(latLng);
            markerOptions.title(nameOfPlaces + ":" +vicinity);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
            mMap.addMarker(markerOptions);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(10));
        }
    }
}
