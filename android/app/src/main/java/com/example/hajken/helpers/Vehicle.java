package com.example.hajken.helpers;

import android.content.Context;
import android.widget.Toast;

import com.example.hajken.MainActivity;
import com.example.hajken.bluetooth.BluetoothConnection;

import es.dmoral.toasty.Toasty;

public class Vehicle implements BluetoothConnection.onBluetoothConnectionListener {

    private static Vehicle mInstance = null;
    private boolean isRunning;
    private Context mContext = MainActivity.getThis();

    //Singleton
    private Vehicle() {
    }

    public static Vehicle getInstance() {
        if (mInstance == null) {
            mInstance = new Vehicle();
        }
        return mInstance;
    }


    public boolean isRunning() {
        return isRunning;
    }

    private void setRunning(boolean running) {
        isRunning = running;
    }

    private void showToast(String string) {

        switch (string) {
            case "Connected":
                Toasty.success(mContext, "Connected", Toast.LENGTH_LONG).show();
                break;
            case "NotConnected":
                Toasty.error(mContext, "Not connected", Toast.LENGTH_LONG).show();
                break;
            case "Running":
                Toasty.info(mContext, "Vehicle running", Toast.LENGTH_LONG).show();
                break;
            case "Stopping":
                Toasty.info(mContext, "Vehicle stopping", Toast.LENGTH_LONG).show();
                break;
            case "Obstacle":
                Toasty.info(mContext, "Obstacle found", Toast.LENGTH_LONG).show();
                break;
        }
    }

    @Override
    public void onConnect() {
        showToast("Connected");
    }

    @Override
    public void onNotConnected() {
        showToast("NotConnected");
    }

    @Override
    public void onCarRunning() {
        setRunning(true);
        showToast("Running");
    }

    @Override
    public void onCarNotRunning() {
        setRunning(false);
        showToast("Stopping");
    }

    @Override
    public void onFoundObstacle() {
        showToast("Obstacle");
    }
}


