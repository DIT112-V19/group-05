package com.example.hajken.fragments;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.hajken.bluetooth.Bluetooth;
import com.example.hajken.bluetooth.BluetoothConnection;
import com.example.hajken.helpers.ListOfDevices;
import com.example.hajken.MainActivity;
import com.example.hajken.R;

import java.util.ArrayList;
import java.util.UUID;

public class ScanFragment extends Fragment implements View.OnClickListener{

    //used to keep track of where we are in LogCat
    private static final String TAG = "ScanFragment";
    private MainActivity mMainActivity;

    //buttons and entities in collFragment
    private Button scanButton, pairButton, unpairButton, routesButton;
    private ListView mListView;
    ArrayList<BluetoothDevice> mBluetoothdevices = new ArrayList<>();
    ListOfDevices mListAdapter;
    BluetoothDevice mPairedBluetoothDevice;
    BluetoothConnection mBluetoothConnection;
    private final static UUID MY_UUID_INSECURE = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    //onCreate is called before onCreateView
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_scan,container,false);

        //Creates the buttons and listview of fragment
        scanButton = view.findViewById(R.id.scan_button);
        unpairButton = view.findViewById(R.id.unpair_button);
        routesButton = view.findViewById(R.id.routes_button);
        mListView = view.findViewById(R.id.device_list);

        //Enables functions to buttons
        scanButton.setOnClickListener(this);
        routesButton.setOnClickListener(this);
        unpairButton.setOnClickListener(this);

        //Changes states of buttons depending on current bluetooth connection
        unPairButtonState();
        routesButtonState();

        //Enables connect possibilities of bluetooth devices and displays devices
        mListAdapter = new ListOfDevices(getContext(), R.layout.listview_item, mBluetoothdevices);
        mListView.setAdapter(mListAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

               //Bluetooth.getInstance().addToListView(getContext(),i);
                Bluetooth.getInstance().getmBluetoothAdapter().cancelDiscovery();
                String deviceName = mBluetoothdevices.get(i).getName();

                //the bond can only be created if the API are correct
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2){
                    mBluetoothdevices.get(i).createBond();
                    mPairedBluetoothDevice = mBluetoothdevices.get(i);
                    mBluetoothConnection = BluetoothConnection.getInstance(getContext());
                    mBluetoothConnection.startClient(mBluetoothdevices.get(i), MY_UUID_INSECURE );
                    Log.i(TAG, " connected to " + deviceName);

                    //changes the states of the button when the connection is completed (correct location?)
                    unPairButtonState();
                    routesButtonState();

                }
            }});

        return view;
    }

    public void unPairButtonState(){

        if (BluetoothConnection.getInstance(getContext()).getIsConnected()){
            unpairButton.setClickable(true);
            unpairButton.setActivated(true);
        } else {
            unpairButton.setClickable(false);
            unpairButton.setClickable(false);
        }
    }

    public void routesButtonState(){

        if (BluetoothConnection.getInstance(getContext()).getIsConnected()){
            routesButton.setClickable(true);
            routesButton.setActivated(true);
        } else {
            routesButton.setClickable(false);
            routesButton.setClickable(false);
        }
    }

    //onAttach is called before onCreate so that we instantiate the interface before using it
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mMainActivity = (MainActivity) getActivity();
        Bluetooth.initialize(getContext());
    }

    @Override
    public void onClick(View view) {
        //This is the events that are associated with the buttons
        switch (view.getId()){
            case R.id.scan_button:{
                mBluetoothdevices.clear();
                Bluetooth.getInstance().enableBluetooth();
                discover();
                unPairButtonState();
                routesButtonState();
                Toast.makeText(getActivity(), "Scanning...", Toast.LENGTH_LONG).show();
                break;
            }
            case R.id.unpair_button:{
                    BluetoothConnection.getInstance(getContext()).unPair(mPairedBluetoothDevice);
                    Toast.makeText(getActivity(), "Closed connection...", Toast.LENGTH_LONG).show();
                break;
            }
            case R.id.routes_button:{
                mMainActivity.inflateFragment(getString(R.string.gateway_fragment));
                break;
            }
        }
    }



    BroadcastReceiver mBroadcastReceiver1 = new BroadcastReceiver() {
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

    public void discover(){

        if (Bluetooth.getInstance().getmBluetoothAdapter().isDiscovering()){
            IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            mMainActivity.registerReceiver(mBroadcastReceiver1, intentFilter);

        }
        if (!Bluetooth.getInstance().getmBluetoothAdapter().isDiscovering()){
            Bluetooth.getInstance().getmBluetoothAdapter().startDiscovery();
            IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            mMainActivity.registerReceiver(mBroadcastReceiver1, intentFilter);
        }
    }


}


