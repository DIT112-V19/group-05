package com.example.hajken;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;



import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import static android.content.ContentValues.TAG;


/**
 * A simple {@link Fragment} subclass.
 */

public class GoogleMapsFragment extends Fragment  implements View.OnClickListener, OnMapReadyCallback {

    private MapView mMapView;
    private BluetoothConnection bluetoothConnection;

    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";


    public GoogleMapsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View view = inflater.inflate(R.layout.fragment_google_maps, container, false);

       mMapView = new MapView(getContext());
       mMapView = view.findViewById(R.id.mapView);


        Bundle mapViewBundle = null;

        if (savedInstanceState != null) {

            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);

        }


        mMapView.onCreate(mapViewBundle);

        mMapView.getMapAsync(this);

        //SupportMapFragment mapFragment =    (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapView);
        //mapFragment.getMapAsync(this);



        return view;
    }


    public void onAttach(Context context){
        super.onAttach(context);

        bluetoothConnection = BluetoothConnection.getInstance(getContext());
        bluetoothConnection.startCar("g!"); //small g to request GPS
        Log.d(TAG, "Request for GPS-message sent");

        String GPS = bluetoothConnection.readGPS();
        Log.d(TAG, "Received this GPS-message from car: " + GPS);

        //For tesing purposes
        //String rawData = "57.707005*11.939065";

        String latitude = GPS.substring(0, 8);
        String longitude = GPS.substring(9, 18);

        Double lng = Double.parseDouble(longitude);
        Log.d(TAG, "Longitude is: " + lng);

        Double lat = Double.parseDouble(latitude);
        Log.d(TAG, "Latitude is: " + lat);



    }




    @Override
    public void onClick(View v) {

    }





    @Override

    public void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);



        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);

        if (mapViewBundle == null) {

            mapViewBundle = new Bundle();

            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);

        }



        mMapView.onSaveInstanceState(mapViewBundle);

    }



    public void addCarOnMap(GoogleMap map){


        //Öppna en tråd som hela tiden kallar på GPS-datan
        //Har en while-loop som hela tiden uppdaterar

        BluetoothConnection.getInstance(getContext());


        //String latitude = call for latitude;
        //String longitude = call car for longitude;

        //Double lat = Double.parseDouble(latitude);
        //Double lng = Double.parseDouble(longitude);

        //map.addMarker(new MarkerOptions().position(new LatLng(lat, lat)).title("THE HAJKEN CAR"));


    }


    @Override

    public void onResume() {

        super.onResume();

        mMapView.onResume();

    }



    @Override

    public void onStart() {

        super.onStart();

        mMapView.onStart();

    }



    @Override
    public void onStop() {

        super.onStop();

        mMapView.onStop();

    }



    @Override

    public void onMapReady(GoogleMap map) {

        addCarOnMap(map);


    }



    @Override
    public void onPause() {

        mMapView.onPause();

        super.onPause();

    }



    @Override
    public void onDestroy() {

        mMapView.onDestroy();

        super.onDestroy();

    }



    @Override

    public void onLowMemory() {

        super.onLowMemory();

        mMapView.onLowMemory();

    }

}
