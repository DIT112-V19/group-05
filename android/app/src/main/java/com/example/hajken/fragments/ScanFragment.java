package com.example.hajken.fragments;

import android.bluetooth.BluetoothAdapter;
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
import com.example.hajken.InterfaceMainActivity;
import com.example.hajken.bluetooth.Bluetooth;
import com.example.hajken.bluetooth.BluetoothConnection;
import com.example.hajken.helpers.ListOfDevices;
import com.example.hajken.R;
import java.util.ArrayList;
import java.util.UUID;
import es.dmoral.toasty.Toasty;

public class ScanFragment extends Fragment implements View.OnClickListener, BluetoothConnection.onBluetoothConnectionListener{

    private static final String TAG = "ScanFragment";
    private Button scanButton, unPairButton, routesButton;
    private ListView mListView;
    private Context mContext;
    private ArrayList<BluetoothDevice> mBluetoothDevices = new ArrayList<>();
    private ListOfDevices mListAdapter;
    private BluetoothDevice mPairedBluetoothDevice;
    private BluetoothConnection mBluetoothConnection;
    private InterfaceMainActivity mInterfaceMainActivity;
    private final static UUID MY_UUID_INSECURE = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Override
    public void onAttach(Context context) {
        Log.d(TAG, "TAG scanFragment - onAttach");

        super.onAttach(context);
        mContext = context;
        mInterfaceMainActivity = (InterfaceMainActivity) getActivity();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        Log.d(TAG, "TAG scanFragment - onCreate");

        super.onCreate(savedInstanceState);


        /// THE FOLLOWING SHOULD BE CLEANED UP the following check
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(getContext(),"No bluetooth,",Toast.LENGTH_LONG).show();
        } else {
            if (!mBluetoothAdapter.isEnabled()) {
                Toast.makeText(getContext(),"Turn on bluetooth,",Toast.LENGTH_LONG).show();
            } else {
                BluetoothConnection.getInstance(getContext()).registerListener(this);
            }
        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        Log.d(TAG, "TAG scanFragment - onCreateView");

        View view = inflater.inflate(R.layout.fragment_scan,container,false);

        //Creates the buttons and listview of fragment
        scanButton = view.findViewById(R.id.scan_button);
        unPairButton = view.findViewById(R.id.unpair_button);
        routesButton = view.findViewById(R.id.routes_button);
        mListView = view.findViewById(R.id.device_list);

        //Enables functions to buttons
        scanButton.setOnClickListener(this);
        routesButton.setOnClickListener(this);
        unPairButton.setOnClickListener(this);

        //Sets the state of buttons upon inflation
        checkStateOfButtons();

        return view;
    }

    public void checkStateOfButtons(){
        Log.d(TAG, "TAG scanFragment - checkStateOfButtons");


        //RoutesButton always active
        routesButton.setClickable(true);
        routesButton.setActivated(true);

        /// THE FOLLOWING SHOULD BE CLEANED UP the following check


        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toasty.error(mContext,"No bluetooth available",Toast.LENGTH_LONG).show();
        } else {
            if (!mBluetoothAdapter.isEnabled()) {
                Toast.makeText(getContext(),"Turn on bluetooth,",Toast.LENGTH_LONG).show();
            } else {
                //Changes the state of the buttons whether or not there is bluetooth connected
                if (BluetoothConnection.getInstance(getContext()).getIsConnected()){
                    unPairButton.setClickable(true);
                    unPairButton.setActivated(true);
                    scanButton.setClickable(false);
                    scanButton.setActivated(false);
                } else {
                    unPairButton.setClickable(false);
                    unPairButton.setActivated(false);
                    scanButton.setClickable(true);
                    scanButton.setActivated(true);
                }
            }
        }
    }

    @Override
    public void onClick(View view) {
        Log.d(TAG, "TAG scanFragment - onClick");


        //These are the events that are associated with the buttons
        switch (view.getId()){
            case R.id.scan_button:{
                Bluetooth.getInstance(getContext()).enableBluetooth();

                //Scans for active bluetooth devices
                mBluetoothDevices.clear();
                discover();
                enableListView();
                break;
            }
            case R.id.unpair_button:{

                //Un-pairs active bluetooth connection
                BluetoothConnection.getInstance(getContext()).unPair(mPairedBluetoothDevice);
                break;
            }
            case R.id.routes_button:{

                //Inflates gateway-fragment
                mInterfaceMainActivity.inflateFragment(getString(R.string.gateway_fragment));
                break;
            }
        }
    }

    public BroadcastReceiver mBroadcastReceiver1 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "TAG scanFragment - Broadcast receiver onReceive");

            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getName() != null){
                    mBluetoothDevices.add(device);
                }
                //only create once and notify when changes occurs
                mListAdapter.notifyDataSetChanged();
            }
        }
    };

    public void discover(){

        if (Bluetooth.getInstance(getContext()).getmBluetoothAdapter().isDiscovering()){
            IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            mInterfaceMainActivity.registerReceiver(mBroadcastReceiver1, intentFilter);
        }
        if (!Bluetooth.getInstance(getContext()).getmBluetoothAdapter().isDiscovering()){
            Bluetooth.getInstance(getContext()).getmBluetoothAdapter().startDiscovery();
            IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            mInterfaceMainActivity.registerReceiver(mBroadcastReceiver1, intentFilter);
        }
    }

    public void enableListView(){

        //Enables connect possibilities of bluetooth devices and displays devices
        mListAdapter = new ListOfDevices(getContext(), R.layout.listview_item, mBluetoothDevices);
        mListView.setAdapter(mListAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Bluetooth.getInstance(getContext()).getmBluetoothAdapter().cancelDiscovery();
                String deviceName = mBluetoothDevices.get(i).getName();

                //the bond can only be created if the API are correct
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2){
                    mBluetoothDevices.get(i).createBond();
                    mPairedBluetoothDevice = mBluetoothDevices.get(i);
                    mBluetoothConnection = BluetoothConnection.getInstance(getContext());
                    mBluetoothConnection.startClient(mBluetoothDevices.get(i), MY_UUID_INSECURE );
                    Log.i(TAG, " connected to " + deviceName);
                }
            }});
    }

    @Override
    public void onConnect() {
        Log.d(TAG, "TAG scanFragment - onConnect");
            mInterfaceMainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    BluetoothConnection.getInstance(getContext()).setIsConnected(true);
                    checkStateOfButtons();
                    Toasty.success(mContext, "Connected to " + mPairedBluetoothDevice.getName(), Toast.LENGTH_LONG).show();
                }
            });
    }

    @Override
    public void onNotConnected() {
        Log.d(TAG, "TAG scanFragment - onNotConnected");

        mInterfaceMainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                BluetoothConnection.getInstance(getContext()).setIsConnected(false);
                checkStateOfButtons();
                BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();


                if (BluetoothConnection.getInstance(getContext()).getWasUnPaired()) {
                    Toasty.info(mContext, "Unpaired with " + mPairedBluetoothDevice.getName(), Toast.LENGTH_LONG).show();
                } else {
                    if (mBluetoothAdapter.isEnabled()){
                        Toasty.error(mContext, "Lost connection with " + mPairedBluetoothDevice.getName(), Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }
}