package com.example.hajken.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.util.Log;

import com.example.hajken.InterfaceMainActivity;
import com.example.hajken.MainActivity;
import com.example.hajken.fragments.ScanFragment;
import com.example.hajken.helpers.ListOfDevices;
import java.util.ArrayList;
import java.util.UUID;

public class Bluetooth {

    private static final String TAG = "BluetoothClass: ";
    private MainActivity mMainActivity = MainActivity.getThis();
    private final static UUID MY_UUID_INSECURE = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private BluetoothDevice mBluetoothDevice;
    private BluetoothDevice mPairedBluetoothDevice;
    private BluetoothAdapter mBluetoothAdapter;
    private ArrayList<BluetoothDevice> mBluetoothdevices = new ArrayList<>();
    private static Bluetooth mInstance = null;
    private ListOfDevices mListAdapter;
    private BluetoothConnection mBluetoothConnection;
    private InterfaceMainActivity mInterfaceMainActivity;
    private Context mContext;

    public Bluetooth(Context context, InterfaceMainActivity mInterfaceMainActivity){
        context = context;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        this.mInterfaceMainActivity = mInterfaceMainActivity ;
    }

    public static Bluetooth getInstance(Context context, InterfaceMainActivity mInterfaceMainActivity){

        if (mInstance == null){
            mInstance = new Bluetooth(context, mInterfaceMainActivity);
        }
        return mInstance;
    }

    public void enableBluetooth(){

        if (mBluetoothAdapter == null){
            Log.d(TAG, "No bluetooth exists");
        }

        if(mBluetoothAdapter.isEnabled()){
            Log.d(TAG, " is already enabled");

        } else{
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            mMainActivity.startActivity(intent);
        }
    }

    public void unPairDevice(BluetoothDevice mPairedBluetoothDevice){
        mBluetoothConnection.unPair(mPairedBluetoothDevice);

    }

    public void stopCar(String input){
        mBluetoothConnection.stopCar(input);
    }

    public void startCar(String input){
        mBluetoothConnection.startCar(input);
    }

    public BroadcastReceiver mBroadcastReceiver1 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "TAG scanFragment - Broadcast receiver onReceive");

            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getName() != null){
                    mBluetoothdevices.add(device);
                }
                //only create once and notify when changes occurs
                mListAdapter.notifyDataSetChanged();
            }
        }
    };

    public void discover(){

        if (getmBluetoothAdapter().isDiscovering()){
            IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            mInterfaceMainActivity.registerReceiver(mBroadcastReceiver1, intentFilter);
        }
        if (!getmBluetoothAdapter().isDiscovering()){
            getmBluetoothAdapter().startDiscovery();
            IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            mInterfaceMainActivity.registerReceiver(mBroadcastReceiver1, intentFilter);
        }
    }


    public void bondWithDevice(Context context, int i){
        mBluetoothAdapter.cancelDiscovery();
        String deviceName = mBluetoothdevices.get(i).getName();

        //the bond can only be created if the API are correct
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2){
            mBluetoothdevices.get(i).createBond();
            mPairedBluetoothDevice = mBluetoothdevices.get(i);
            mBluetoothConnection = BluetoothConnection.getInstance(context);
            mBluetoothConnection.startClient(mBluetoothdevices.get(i), MY_UUID_INSECURE );
            Log.i(TAG, " connected to " + deviceName);

        }
    }

    public void registerListenerBluetooth(BluetoothConnection.onBluetoothConnectionListener listener){
        mBluetoothConnection.registerListener(listener);
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

    public ArrayList<BluetoothDevice> getmBluetoothdevices() {
        return mBluetoothdevices;
    }

    public void setmBluetoothdevices(ArrayList<BluetoothDevice> mBluetoothdevices) {
        this.mBluetoothdevices = mBluetoothdevices;
    }

    public static UUID getMyUuidInsecure() {
        return MY_UUID_INSECURE;
    }


    public ListOfDevices getmListAdapter() {
        return mListAdapter;
    }

    public void setmListAdapter(ListOfDevices mListAdapter) {
        this.mListAdapter = mListAdapter;
    }

    public BluetoothConnection getmBluetoothConnection() {
        return mBluetoothConnection;
    }

    public void setmBluetoothConnection(BluetoothConnection mBluetoothConnection) {
        this.mBluetoothConnection = mBluetoothConnection;
    }

    public BluetoothDevice getmPairedBluetoothDevice() {
        return mPairedBluetoothDevice;
    }
}
