package com.example.hajken.helpers;

import android.content.Context;
import android.util.Log;

import com.example.hajken.bluetooth.BluetoothConnection;

import com.example.hajken.InterfaceMainActivity;
import com.example.hajken.bluetooth.BluetoothConnection;

import static android.content.ContentValues.TAG;

public class GPSConnector {

    private double lat;
    private double lgn;
    private String GPSstring;
    private String sLat;
    private String sLgn;

    private Boolean isUpdated;

    private static GPSConnector mInstance = null;
    private Context myContext;

    private GPSConnector(Context context) {
        myContext = context;
    }

    public static GPSConnector getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new GPSConnector(context);
        }
        return mInstance;
    }


    private void connect() {

        //BluetoothConnection bluetoothConnection = BluetoothConnection.getInstance();

        Log.d(TAG, "Request for GPS-message sent");

        do {

            // bluetoothConnection.startCar("g!"); //small g to request GPS

            // GPSstring = bluetoothConnection.readGPS();

        } while (GPSstring != null);

    }


    public void update() {

        connect();
        setLatLgn();

        isUpdated = true;
    }

    private void setLatLgn() {

        int index = GPSstring.indexOf("*");

        sLat = GPSstring.substring(0, index);
        sLgn = GPSstring.substring(index + 1);

        setLat(Double.parseDouble(sLat));
        setLgn(Double.parseDouble(sLgn));
    }

    public boolean isUpdated() {
        return isUpdated;
    }

    private void setLat(double lat) {

        this.lat = lat;
    }

    private void setLgn(double lat) {

        this.lat = lat;
    }

    public double getLat() {

        return lat;
    }

    public double getLgn() {

        return lgn;
    }


    public void GPSTrackerTry() {

        //CallingTheGPSTracker
        GPSTracker myTracker = new GPSTracker(myContext);
        myTracker.getLocation().getLatitude();
        myTracker.getLocation().getLongitude();


    }

}
