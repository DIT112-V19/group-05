package com.example.hajken.fragments;


import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PointF;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hajken.BuildConfig;
import com.google.maps.android.SphericalUtil;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;

import com.example.hajken.InterfaceMainActivity;
import com.example.hajken.bluetooth.BluetoothConnection;
import com.example.hajken.R;
import com.example.hajken.helpers.CoordinateConverter;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.TravelMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static android.content.ContentValues.TAG;


/**
 * A simple {@link Fragment} subclass.
 */

public class GoogleMapsFragment extends Fragment  implements View.OnClickListener, OnMapReadyCallback {

    private final int SLOW = 1;
    private final int MED = 2;
    private final int FAST = 3;

    private MapView mMapView;
    private BluetoothConnection bluetoothConnection;

    private GeoApiContext mGeoApiContext = null;

    private Button startCarButton;
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private TextView textView;
    //private TextView mApiKeyField;
    private String instructions;


    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    private InterfaceMainActivity interfaceMainActivity;


    //Marker for the destination of the car
    private Marker destinationMarker = null;
    private Marker carMarker = null;

    //Creating a Polyline
    Polyline polyline = null;

    //Creating the map
    GoogleMap map;

    //Creating an arrayList to hold coordinates of PolyLines
    List<LatLng> newDecodedPath = new ArrayList<>();

    public GoogleMapsFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_google_maps, container, false);

        //Creates the buttons
        startCarButton = view.findViewById(R.id.start_car_button);
        textView = view.findViewById(R.id.device_map_fragment);

        //TextView for API-key
        //mApiKeyField = view.findViewById(R.id.apiKeyText);
        //mApiKeyField.setText("API key: " + BuildConfig.apiKey);

        //Speed changing
        radioGroup = view.findViewById(R.id.radio_group);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton checkedRadioButton = group.findViewById(checkedId);
                boolean isChecked = checkedRadioButton.isChecked();

                if (isChecked) {
                    checkButton(view);
                }
            }
        });

        if (BluetoothConnection.getInstance(getContext()).getIsConnected()) {
            textView.setText("Connected Device:" + BluetoothConnection.getInstance(getContext()).getDeviceName());
        } else {
            textView.setText("Connected Device: None");
        }

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

        startCarButton.setOnClickListener(this);


        return view;
    }


    public void onAttach(Context context) {
        super.onAttach(context);


        // bluetoothConnection = BluetoothConnection.getInstance(getContext());
        // bluetoothConnection.startCar("g!"); //small g to request GPS
        // Log.d(TAG, "Request for GPS-message sent");

        //bluetoothConnection = BluetoothConnection.getInstance(getContext());
        //bluetoothConnection.startCar("g!"); //small g to request GPS
        Log.d(TAG, "Request for GPS-message sent");

        interfaceMainActivity = (InterfaceMainActivity) getActivity();

        /*
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
        */


        //This might be moved to another method
        /*if (mGeoApiContext == null) {
            mGeoApiContext = new GeoApiContext.Builder()
                    .apiKey(getString(R.string.google_map_api_key))
                    .build();
        }
        */

        if (mGeoApiContext == null) {
            mGeoApiContext = new GeoApiContext.Builder()
                    .apiKey(BuildConfig.apiKey)
                    .build();
        }
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

    public void addCarOnMap(GoogleMap map) {
        //Öppna en tråd som hela tiden kallar på GPS-datan
        //Har en while-loop som hela tiden uppdaterar

        BluetoothConnection.getInstance(getContext());

        //String latitude = call for latitude;
        //String longitude = call car for longitude;

        //Double lat = Double.parseDouble(latitude);
        //Double lng = Double.parseDouble(longitude);


        //Adding car marker and adding on map
        carMarker = map.addMarker(new MarkerOptions().position(new LatLng(57.706931, 11.938822)).title("THE HAJKEN CAR"));

        //This moves the camera and zooms in on myMarker
        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        //the include method will calculate the min and max bound.
        builder.include(carMarker.getPosition());

        LatLngBounds bounds = builder.build();

        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.10); // offset from edges of the map 10% of screen

        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);

        map.animateCamera(cu);

        //GPSCoordinatesToCarInstructions();
    }

    public void checkButton(View view) {
        int radioId = radioGroup.getCheckedRadioButtonId();
        radioButton = view.findViewById(radioId);

        switch (radioButton.getText().toString()) {
            case "Slow": {
                CoordinateConverter.getInstance(getContext()).setSpeed(SLOW);
                break;
            }

            case "Medium": {
                CoordinateConverter.getInstance(getContext()).setSpeed(MED);
                break;
            }

            case "High": {
                CoordinateConverter.getInstance(getContext()).setSpeed(FAST);
                break;
            }
        }

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            //This is the events that are associated with the buttons

            case R.id.start_car_button: {

                if (BluetoothConnection.getInstance(getContext()).getIsConnected()) {

                    Toast.makeText(getActivity(), "Starting Car. No funcion yet.", Toast.LENGTH_SHORT).show();

                    break;

                } else {
                    Toast.makeText(getActivity(), "Not connected to a device. No funcion yet.", Toast.LENGTH_LONG).show();
                    break;

                }
            }

        }

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

        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title(latLng.latitude + " : " + latLng.longitude);
                map.animateCamera(CameraUpdateFactory.newLatLng(latLng));

                if (destinationMarker == null) {
                    destinationMarker = map.addMarker(markerOptions);
                } else {
                    destinationMarker.remove();
                    destinationMarker = map.addMarker(markerOptions);
                }

                Log.d(TAG, "Getting to calculateDirections()");

                //calculateDirections(destinationMarker);
                calcluateDirections2(map, destinationMarker);

            }
        });

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


    private void calcluateDirections2(GoogleMap map, Marker destinationMarker) {

        DirectionsApiRequest apiRequest = DirectionsApi.newRequest(mGeoApiContext);
        apiRequest.origin(new com.google.maps.model.LatLng(carMarker.getPosition().latitude, carMarker.getPosition().longitude));
        apiRequest.destination(new com.google.maps.model.LatLng(destinationMarker.getPosition().latitude, destinationMarker.getPosition().latitude));
        apiRequest.mode(TravelMode.BICYCLING); //set travelling mode
        apiRequest.alternatives(false);


        //Creating a LatLng object to hold destination coordinates
        com.google.maps.model.LatLng destination = new com.google.maps.model.LatLng(
                destinationMarker.getPosition().latitude,
                destinationMarker.getPosition().longitude
        );

        apiRequest.destination(destination).setCallback(new com.google.maps.PendingResult.Callback<DirectionsResult>() {
            @Override
            public void onResult(DirectionsResult result) {
                DirectionsRoute[] routes = result.routes;

                Log.d(TAG, "onResult: " + result.routes[0].toString());

                System.out.println("Result of routeObject is" + routes[0].toString());

                //Passing a DirectionResult
                addPolylinesToMap(result, map);

            }

            @Override
            public void onFailure(Throwable e) {

            }
        });

    }


    private void addPolylinesToMap(final DirectionsResult result, GoogleMap map) {



        new Handler(Looper.getMainLooper()).post(new Runnable() {

            @Override
            //Everything inside run will run on main thread
            public void run() {

                Log.d(TAG, "run: result routes: " + result.routes.length);

                for (DirectionsRoute route : result.routes) {

                    Log.d(TAG, "run: leg: " + route.legs[0].toString());

                    List<com.google.maps.model.LatLng> decodedPath = PolylineEncoding.decode(route.overviewPolyline.getEncodedPath());

                    setDecodedPath(decodedPath);


                    if (polyline != null) {
                        if (polyline.isClickable()) {
                            polyline.remove();
                        }
                    }


                    //Here Polyline gets put onto the Map
                    polyline = map.addPolyline(new PolylineOptions().addAll(getDecodedPath()));

                    polyline.setColor(ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark));

                    polyline.setClickable(true);

                    printDecodedPath();

                    //This moves the camera to show the entire polyline on the screen
                    moveToBounds(polyline, map);

                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //printPoints(map);

                }

            }

        });

    }

    public void setDecodedPath(List<com.google.maps.model.LatLng> decodedPath) {

        newDecodedPath.clear();

        // This loops through all the LatLng coordinates of ONE polyline.
        for (com.google.maps.model.LatLng latLng : decodedPath) {

            newDecodedPath.add(new LatLng(
                    latLng.lat,
                    latLng.lng
            ));
        }

    }


    public void printDecodedPath() {

        //Printing coordinates in console
        for (LatLng lg : newDecodedPath) {
            System.out.println("PRINTING : Latitude " + lg.latitude + " Longitude + " + lg.longitude);
        }

    }

    public List<LatLng> getDecodedPath() {

        return newDecodedPath;
    }


    //The marker should be retrieved from the onMapClick Event
    private void calculateDirections(Marker marker) {

        Log.d(TAG, "calculateDirections: calculating directions.");

        //Destination is the position where the car should be driving
        com.google.maps.model.LatLng destination = new com.google.maps.model.LatLng(

                marker.getPosition().latitude,
                marker.getPosition().longitude

        );

        Log.d(TAG, "Position of latitide: " + marker.getPosition().latitude);


        DirectionsApiRequest directions = new DirectionsApiRequest(mGeoApiContext);

        // This shows all possible routes. If its set to false, only one route will show
        directions.alternatives(true);

        //Declaring for testing below
        double latitude = 57.42254;
        double longitude = 11.56120;

        //This is from where the user is coming from. Needs to be where the car is located.
        directions.origin(

                new com.google.maps.model.LatLng(


                        //Latitude, hardcoded above
                        latitude,
                        //Longitude, hardcoded above
                        longitude

                        //Example code to be deleted

                        //Getting from Firebase
                        //mUserPosition.getGeo_point().getLatitude(),
                        //mUserPosition.getGeo_point().getLongitude()

                )

        );


        //Calculating directions
        Log.d(TAG, "calculateDirections: destination: " + destination.toString());
        System.out.println("Calculating Directions");

        //Something like this to set the MODE
        // apiRequest.mode(TravelMode.DRIVING); //set travelling mode

        directions.destination(destination).setCallback(new PendingResult.Callback<DirectionsResult>() {

            @Override

            public void onResult(DirectionsResult result) {

                Log.d(TAG, "calculateDirections: routes: " + result.routes[0].toString());

                Log.d(TAG, "calculateDirections: duration: " + result.routes[0].legs[0].duration);

                Log.d(TAG, "calculateDirections: distance: " + result.routes[0].legs[0].distance);

                Log.d(TAG, "calculateDirections: geocodedWayPoints: " + result.geocodedWaypoints[0].toString());

            }


            @Override

            public void onFailure(Throwable e) {

                Log.e(TAG, "calculateDirections: Failed to get directions: " + e.getMessage());

            }

        });

    }






    //Move camera of Maps fit the screen
    private void moveToBounds(Polyline p, GoogleMap map){

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for(int i = 0; i < p.getPoints().size();i++){
            builder.include(p.getPoints().get(i));
        }

        LatLngBounds bounds = builder.build();
        int padding = 50; // offset from edges of the map in pixels

        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        map.animateCamera(cu);
    }

}
