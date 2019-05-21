package com.example.hajken.fragments;


import android.content.Context;
import android.graphics.Point;
import android.graphics.PointF;
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

import android.widget.SeekBar;
import android.widget.Toast;

import com.example.hajken.BuildConfig;
import com.example.hajken.helpers.GPSTracker;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
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
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.TravelMode;

import java.util.ArrayList;
import java.util.List;
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

    private GeoApiContext mGeoApiContext = null;

    private Button startCarButton;
    private RadioGroup radioGroup;
    private RadioButton radioButton;

    private TextView textView;
    //private TextView mApiKeyField;

    private String instructions;
    private TextView amountOfLoops;
    private SeekBar seekBar;

    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    private InterfaceMainActivity interfaceMainActivity;

    //Marker for the destination of the car
    private Marker destinationMarker = null;
    private Marker carMarker = null;
    private static final float carMarkerColor = 260f;

    //Creating a Polyline
    Polyline polyline = null;

    //Creating the map
    GoogleMap map;

    //Creating an arrayList to hold coordinates of PolyLines
    List<LatLng> newDecodedPath = new ArrayList<>();

    //ArrayList to hold xy-coordinates of path
    ArrayList<PointF> pathToPointF;

    public GoogleMapsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_google_maps, container, false);

        //Creates the buttons
        startCarButton = view.findViewById(R.id.start_car_button);
        amountOfLoops = view.findViewById(R.id.amount_of_repetitions);
        seekBar = view.findViewById(R.id.seekbar);
        startCarButton.setClickable(false);
        startCarButton.setActivated(false);

        //TextView for API-key
        //mApiKeyField = view.findViewById(R.id.apiKeyText);
        //mApiKeyField.setText("API key: " + BuildConfig.apiKey);

        //Speed changing
        radioGroup = view.findViewById(R.id.radio_group);

        //Set amount of repetitions beginning at zero
        amountOfLoops.setText(getString(R.string.amount_of_repetitions,Integer.toString(0)));

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


        mMapView = new MapView(getContext());
        mMapView = view.findViewById(R.id.mapView);

        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }

        mMapView.onCreate(mapViewBundle);
        mMapView.getMapAsync(this);

        startCarButton.setOnClickListener(this);

        seekBar.setMax(10);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                CoordinateConverter.getInstance(getContext()).setNrOfLoops(progress);
                amountOfLoops.setText(getString(R.string.amount_of_repetitions,Integer.toString(progress)));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        return view;
    }


    public void onAttach(Context context) {
        super.onAttach(context);


        //Call GPSConnector.update()

        //This might be moved to another method
        //Might be a singleton pattern?
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

        BluetoothConnection.getInstance(getContext());

        //
        GPSTracker myTracker = new GPSTracker(getContext());
        myTracker.getLocation();


        //Setting the CarMarker to its LatLgn Position
        carMarker = map.addMarker(new MarkerOptions().position(new LatLng(myTracker.getLocation().getLatitude(), myTracker.getLocation().getLongitude()))
                .title("THE HAJKEN CAR").icon(BitmapDescriptorFactory.defaultMarker(carMarkerColor)));

        //Moving the camera to zoom value 19
        CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(carMarker.getPosition(), 19.0f);

        map.animateCamera(cu);
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

            case "Fast": {
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

                instructions = CoordinateConverter.getInstance(getContext()).returnInstructions(getPathToPointFList());
                System.out.println("Instruction coordinates i Maps är: " + instructions);

                if (BluetoothConnection.getInstance(getContext()).getIsConnected()) {
                    BluetoothConnection.getInstance(getContext()).startCar(instructions);
                    break;

                } else {
                    Toast.makeText(getActivity(), "Not connected to a device. No function yet.", Toast.LENGTH_LONG).show();
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

                if (destinationMarker == null) {
                    destinationMarker = map.addMarker(markerOptions);
                } else {
                    destinationMarker.remove();
                    destinationMarker = map.addMarker(markerOptions);
                }

                startCarButton.setActivated(true);
                startCarButton.setClickable(true);

                Log.d(TAG, "Getting to calculateDirections()");

                //calculateDirections(destinationMarker);
                calculateDirections(map, destinationMarker);

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


    private void calculateDirections(GoogleMap map, Marker destinationMarker) {

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

                    //Only works if the route chosen is below 100 meters
                    if(SphericalUtil.computeLength(getDecodedPath()) < 100){

                        //Here Polyline gets put onto the Map
                        polyline = map.addPolyline(new PolylineOptions().addAll(getDecodedPath()));

                        polyline.setColor(ContextCompat.getColor(getActivity(), R.color.background_color));

                        polyline.setClickable(true);

                        //Only for developing
                        printDecodedPath();

                        //This moves the camera to show the entire polyline on the screen
                        moveToBounds(polyline, map);

                        try {
                            TimeUnit.SECONDS.sleep(1);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        convertLatLangToPoints(map);

                    }else{
                        Toast.makeText(getActivity(), "Please choose a destination within 100 meters to create a route for the car", Toast.LENGTH_LONG).show();
                    }

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


    //Only for developing
    public void printDecodedPath() {

        //Printing coordinates in console
        for (LatLng lg : newDecodedPath) {
            System.out.println("PRINTING : Latitude " + lg.latitude + " Longitude + " + lg.longitude);
        }
    }

    public List<LatLng> getDecodedPath() {
        return newDecodedPath;
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


    public void convertLatLangToPoints(GoogleMap map){

        List<LatLng> decodedPath = getDecodedPath();

        //Getting the distance of of the entire map
        LatLng farLeft = map.getProjection().getVisibleRegion().farLeft;
        LatLng farRight = map.getProjection().getVisibleRegion().farRight;

        //Only for developing
        double distanceOfRoute = SphericalUtil.computeDistanceBetween(decodedPath.get(0), decodedPath.get(1));
        //System.out.println("The distance of the map in meters is " + distanceOfMap);
        System.out.println("Distansen på rutten är " + distanceOfRoute);

        //Converting the distance between the entire map into a scale
        double scale = SphericalUtil.computeDistanceBetween(farLeft, farRight) / 9;

        double xCoordinate = 0;
        double yCoordinate = 0;

        //Initializing the ArrayList
        pathToPointF = new ArrayList<>();

        for(int i = 0; i<decodedPath.size(); i++){

            //System.out.println("Printing point for coordinate number " + i+1 + map.getProjection().toScreenLocation(decodedPath.get(i)) + " LATLGNG is: " + decodedPath.get(i));

            //Creating coordinates that works in a scale 1:1  -  1x = 1 cm in reality
            Point point = map.getProjection().toScreenLocation(decodedPath.get(i));
            xCoordinate = point.x * scale;
            yCoordinate = 900-point.y * scale;

            int x = (int) xCoordinate;
            int y = (int) yCoordinate;

            PointF convertedPoint = new PointF(x, y);

            pathToPointF.add(convertedPoint);

        }

        for(int i = 0; i<pathToPointF.size(); i++){
            System.out.println("Mina slutgiltiga koordinater är nu: x = " + pathToPointF.get(i).x + " och y = " + pathToPointF.get(i).y);
        }

    }


    public void  setCarMarker(){
    }

    public Marker getCarMarker(){
        return carMarker;
    }


    public ArrayList<PointF> getPathToPointFList(){
        return pathToPointF;
    }

}
