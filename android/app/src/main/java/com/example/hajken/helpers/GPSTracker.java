package com.example.hajken.helpers;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.example.hajken.InterfaceMainActivity;

import java.util.concurrent.TimeUnit;

import static android.support.constraint.Constraints.TAG;


public class GPSTracker extends Service implements LocationListener {

    private final Context mContext;

    // Flag for GPS status
    private boolean isGPSEnabled = false;

    // Flag for network status
    private boolean isNetworkEnabled = false;

    // Flag for GPS status
    private boolean canGetLocation = false;

    private Location location; // Location
    private double latitude; // Latitude
    private double longitude; // Longitude

    private String GPSstring; //String from Car holding GPS Coordinates
    private String sLat; //For concatenation and casting
    private String sLgn; //For concatenation and casting

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute

    // Declaring a Location Manager
    protected LocationManager locationManager;

    private static GPSTracker mInstance = null;

    private GPSTracker(Context context){
        mContext = context;
    }

    //Singleton pattern
    public static GPSTracker getInstance(Context context){
        if (mInstance == null){
            mInstance = new GPSTracker(context);
        }
        return mInstance;
    }


    public void setLatLgn(){

        Log.d(TAG, "Setting LatLgn");

        //Sleeping for 500 milliseconds to update GPSString
        try {
            TimeUnit.MILLISECONDS.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //if the GPSString has been updated from the car
        if(getGPSstring() != null){

            Log.d(TAG, "Got a GPS position from Car");

            int index = getGPSstring().indexOf("*");

            setsLat(getGPSstring().substring(0,index));
            setsLgn(getGPSstring().substring(index+1));

            setLatitude(Double.parseDouble(getsLat()));
            setLongitude(Double.parseDouble(getsLgn()));

        }
        //else get the GPS position from the users phone
        else {

            Log.d(TAG, "Got a GPS position from Phone");

            setLatitude(getLocation().getLatitude());
            setLongitude(getLocation().getLongitude());

        }

    }


    public Location getLocation() {
        try {
            locationManager = (LocationManager) mContext
                    .getSystemService(LOCATION_SERVICE);

            // Getting GPS status
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // Getting network status
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // No network provider is enabled
            } else {
                this.canGetLocation = true;
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.d("Network", "Network");
                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
                // If GPS enabled, get latitude/longitude using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        Log.d("GPS Enabled", "GPS Enabled");
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                }
            }
        }
        catch (SecurityException e) {
            e.printStackTrace();
        }

        return location;
    }


    /**
     * Stop using GPS listener
     * Calling this function will stop using GPS in your app.
     * */
    public void stopUsingGPS(){
        if(locationManager != null){
            locationManager.removeUpdates(GPSTracker.this);
        }
    }


    /**
     * Function to get latitude
     * */
    public double getLatitude(){
        if(location != null){
            latitude = location.getLatitude();
        }

        // return latitude
        return latitude;
    }


    /**
     * Function to get longitude
     * */
    public double getLongitude(){
        if(location != null){
            longitude = location.getLongitude();
        }

        // return longitude
        return longitude;
    }

    /**
     * Function to check GPS/Wi-Fi enabled
     * @return boolean
     * */
    public boolean canGetLocation() {
        return this.canGetLocation;
    }


    /**
     * Function to show settings alert dialog.
     * On pressing the Settings button it will launch Settings Options.
     * */
    public void showSettingsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings");

        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        // On pressing the Settings button.
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);
            }
        });

        // On pressing the cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }


    public String getGPSstring() {
        return GPSstring;
    }

    public void setGPSstring(String GPSstring) {
        this.GPSstring = GPSstring;
    }

    public String getsLat() {
        return sLat;
    }

    public void setsLat(String sLat) {
        this.sLat = sLat;
    }

    public String getsLgn() {
        return sLgn;
    }

    public void setsLgn(String sLgn) {
        this.sLgn = sLgn;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public void onLocationChanged(Location location) {
    }


    @Override
    public void onProviderDisabled(String provider) {
    }


    @Override
    public void onProviderEnabled(String provider) {
    }


    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }


    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }
}
