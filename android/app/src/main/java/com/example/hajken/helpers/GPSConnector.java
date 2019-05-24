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

    private GPSConnector(Context context){
        myContext = context;
    }

    public static GPSConnector getInstance(Context context){
        if (mInstance == null){
            mInstance = new GPSConnector(context);
        }
        return mInstance;
    }



    public void connect(){

        Log.d(TAG, "Request for GPS-message sent");

        double currentTime = System.currentTimeMillis();
        double endTime = currentTime + 3000;

        //small g to request GPS
        BluetoothConnection.getInstance(myContext).startCar("g!");

        while(System.currentTimeMillis() < endTime || (GPSstring != null && !GPSstring.contains("*"))){

            GPSstring = BluetoothConnection.getInstance(myContext).readGPS();

        }

        System.out.println("GPS STRING IS NOW" + GPSstring);

        if(GPSstring == null){

            System.out.println("Did not get a GPSstring here");

            //Getting Lat and Lgn from phone
            this.lat = GPSTracker.getInstance(myContext).getLocation().getLatitude();
            this.lgn = GPSTracker.getInstance(myContext).getLocation().getLongitude();

        }

    }


    public void update(){

        //connecting to Bluetoth of car
        connect();

        if(GPSstring != null){
            setLatLgn();
        }

        isUpdated = true;
    }

    public void setLatLgn(){

        int index = GPSstring.indexOf("*");

        sLat = GPSstring.substring(0,index);
        sLgn = GPSstring.substring(index + 1);

        setLat(Double.parseDouble(sLat));
        setLgn(Double.parseDouble(sLgn));
    }

    public boolean isUpdated(){
        return isUpdated;
    }

    public void setLat(double lat){

        this.lat = lat;
    }

    public void setLgn(double lat){

        this.lat = lat;
    }

    public double getLat(){

        return lat;
    }

    public double getLgn(){

        return lgn;
    }



}
