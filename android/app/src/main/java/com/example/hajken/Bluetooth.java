package com.example.hajken;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

public class Bluetooth {

    public static final String TAG = "BluetoothClass: ";
    MainActivity mainActivity;

    BluetoothDevice mBluetoothDevice;
    BluetoothAdapter mBluetoothAdapter;
    //public boolean isActivated = false;
    ArrayList<BluetoothDevice> mBluetoothdevices = new ArrayList<>();

    public Bluetooth(Context context, BluetoothAdapter bluetoothAdapter){
        context = context;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public void enableBluetooth(){

        if (mBluetoothAdapter == null){
            Log.d(TAG, "No bluetooth exists");
        }

        if(mBluetoothAdapter.isEnabled()){
            Log.d(TAG, " is already enabled");
            //isActivated = true;

        } else {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            mainActivity.startActivity(intent);
            Log.d(TAG, " Enabled bluetooth");
           // isActivated = true;
        }

    }

    public BluetoothDevice scanForDevices(){

        if (mBluetoothAdapter != null){
            Log.d(TAG, " Bluetooth exists and device can scan");
            if (mBluetoothAdapter.isDiscovering()){
                Log.d(TAG, " Discovering..");
                mBluetoothAdapter.cancelDiscovery();
                mBluetoothAdapter.startDiscovery();
                Intent intent = new Intent(BluetoothDevice.ACTION_FOUND);
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                mBluetoothdevices.add(device);
            }
            if (!mBluetoothAdapter.isDiscovering()){
                Log.d(TAG, " Didn't discover but now is.. ");
                mBluetoothAdapter.startDiscovery();
                Intent intent = new Intent(BluetoothDevice.ACTION_FOUND);
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                return device;
            }
        }
        Log.d(TAG, " This device has no bluetooth");
        return null;
    }





    public BluetoothDevice getmBluetoothDevice() {
        return mBluetoothDevice;
    }

    public void setmBluetoothDevice(BluetoothDevice mBluetoothDevice) {
        this.mBluetoothDevice = mBluetoothDevice;
    }

    public BluetoothAdapter getmBluetoothAdapter() {
        return mBluetoothAdapter;
    }

    public void setmBluetoothAdapter(BluetoothAdapter mBluetoothAdapter) {
        this.mBluetoothAdapter = mBluetoothAdapter;
    }

    /*public boolean isActivated() {
        return isActivated;
    }

    public void setActivated(boolean activated) {
        isActivated = activated;
    }
    */

    public ArrayList<BluetoothDevice> getmBluetoothdevices() {
        return mBluetoothdevices;
    }

    public void setmBluetoothdevices(ArrayList<BluetoothDevice> mBluetoothdevices) {
        this.mBluetoothdevices = mBluetoothdevices;
    }
}
