package com.example.hajken;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.util.Log;

import java.util.ArrayList;
import java.util.UUID;

public class Bluetooth {

    private static final String TAG = "BluetoothClass: ";
    private InterfaceMainActivity mInterface;
    private final static UUID MY_UUID_INSECURE = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");


    private BluetoothDevice mBluetoothDevice;
    private BluetoothAdapter mBluetoothAdapter;
    private ArrayList<BluetoothDevice> mBluetoothdevices = new ArrayList<>();
    private static Bluetooth mInstance = null;
    private ListOfDevices mListAdapter;
    private BluetoothConnection mBluetoothConnection;


    protected Bluetooth(Context context){
        context = context;
       mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    }

    public static Bluetooth getInstance(){

        return mInstance;
    }

    public static void initialize(Context context){

        mInstance = new Bluetooth(context);
    }

    public Intent enableBluetooth(){
        Intent intent = null;

        if (mBluetoothAdapter == null){
            Log.d(TAG, "No bluetooth exists");
            return intent;
        }

        if(mBluetoothAdapter.isEnabled()){
            Log.d(TAG, " is already enabled");
            //isActivated = true;
            return intent;

        } else{


             intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);

            return intent;
           // isActivated = true;
        }

    }

   /* private BroadcastReceiver mBroadcastReceiver1 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
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

  void discover(){

        if (mBluetoothAdapter.isDiscovering()){
            IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            mainActivity.registerReceiver(mBroadcastReceiver1, intentFilter);

        }
        if (!mBluetoothAdapter.isDiscovering()){
            mBluetoothAdapter.startDiscovery();
            IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            mainActivity.registerReceiver(mBroadcastReceiver1, intentFilter);
        }
    }

    void addToListView(Context context, int i){
        mBluetoothAdapter.cancelDiscovery();
        String deviceName = mBluetoothdevices.get(i).getName();

        //the bond can only be created if the API are correct
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2){
            mBluetoothdevices.get(i).createBond();
            mBluetoothConnection = BluetoothConnection.getInstance(context);
            mBluetoothConnection.startClient(mBluetoothdevices.get(i), MY_UUID_INSECURE );
            Log.i(TAG, " connected to " + deviceName);

        }
    }*/


    public BluetoothDevice getmBluetoothDevice() {
        return mBluetoothDevice;
    }

    public void setmBluetoothDevice(BluetoothDevice mBluetoothDevice) {
        this.mBluetoothDevice = mBluetoothDevice;
    }

    protected BluetoothAdapter getmBluetoothAdapter() {
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


   /* public MainActivity getMainActivity() {
        return mainActivity;
    }

    public void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }*/

    public static UUID getMyUuidInsecure() {
        return MY_UUID_INSECURE;
    }

    public static Bluetooth getmInstance() {
        return mInstance;
    }

    public static void setmInstance(Bluetooth mInstance) {
        Bluetooth.mInstance = mInstance;
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

   /* public BroadcastReceiver getmBroadcastReceiver1() {
        return mBroadcastReceiver1;
    }

    public void setmBroadcastReceiver1(BroadcastReceiver mBroadcastReceiver1) {
        this.mBroadcastReceiver1 = mBroadcastReceiver1;
    } */
}
