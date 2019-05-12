package com.example.hajken.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


import com.example.hajken.InterfaceMainActivity;
import com.example.hajken.bluetooth.BluetoothConnection;
import com.example.hajken.R;
import com.example.hajken.helpers.CoordinateConverter;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

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
    private Button startCarButton;
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private String instructions;
    private TextView amountOfLoops;
    private SeekBar seekBar;

    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    private InterfaceMainActivity interfaceMainActivity;

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

        //Speed changing
        radioGroup = view.findViewById(R.id.radio_group);

        //Set amount of repetitions beginning at zero
        amountOfLoops.setText(getString(R.string.amount_of_repetitions,Integer.toString(0)));

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton checkedRadioButton = group.findViewById(checkedId);
                boolean isChecked = checkedRadioButton.isChecked();

                if (isChecked){
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

        //SupportMapFragment mapFragment =    (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapView);
        //mapFragment.getMapAsync(this);

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


    public void onAttach(Context context){
        super.onAttach(context);

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

        map.addMarker(new MarkerOptions().position(new LatLng(00.000000, 00.000000)).title("THE HAJKEN CAR"));


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
