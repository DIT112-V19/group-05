package com.example.hajken.fragments;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
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
    private ListOfDevices mListAdapter;
    private Bluetooth mBluetooth;
    private InterfaceMainActivity mInterfaceMainActivity;
    private BluetoothAdapter mBluetoothAdapter;

    @Override
    public void onAttach(Context context) {
        Log.d(TAG, "TAG scanFragment - onAttach");

        super.onAttach(context);
        mContext = context;
        mInterfaceMainActivity = (InterfaceMainActivity) getActivity();
        mBluetooth = Bluetooth.getInstance(getContext(), mInterfaceMainActivity);
        mBluetoothAdapter = mBluetooth.getmBluetoothAdapter();

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        Log.d(TAG, "TAG scanFragment - onCreate");

        super.onCreate(savedInstanceState);


        /// THE FOLLOWING SHOULD BE CLEANED UP the following check
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
                mBluetooth.enableBluetooth();

                //Scans for active bluetooth devices
                mBluetooth.getmBluetoothdevices().clear();
                mBluetooth.discover();
                enableListView();
                break;
            }
            case R.id.unpair_button:{

                //Un-pairs active bluetooth connection
                mBluetooth.unPairDevice(mBluetooth.getmPairedBluetoothDevice());
                break;
            }
            case R.id.routes_button:{

                //Inflates gateway-fragment
                mInterfaceMainActivity.inflateFragment(getString(R.string.gateway_fragment));
                break;
            }
        }
    }

    public void enableListView(){

        //Enables connect possibilities of bluetooth devices and displays devices
        mListAdapter = new ListOfDevices(getContext(), R.layout.listview_item, mBluetooth.getmBluetoothdevices());
        mBluetooth.setmListAdapter(mListAdapter);
        mListView.setAdapter(mBluetooth.getmListAdapter());
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mBluetooth.bondWithDevice(getContext(), i);
            }});
    }

    @Override
    public void onConnect() {
        Log.d(TAG, "TAG scanFragment - onConnect");
            mInterfaceMainActivity.runOnUiThread(() -> {
                BluetoothConnection.getInstance(getContext()).setIsConnected(true);
                checkStateOfButtons();
                Toasty.success(mContext, "Connected to " + mBluetooth.getmPairedBluetoothDevice().getName(), Toast.LENGTH_LONG).show();
            });
    }

    @Override
    public void onNotConnected() {
        Log.d(TAG, "TAG scanFragment - onNotConnected");

        mInterfaceMainActivity.runOnUiThread(() -> {
            BluetoothConnection.getInstance(getContext()).setIsConnected(false);
            checkStateOfButtons();

            if (BluetoothConnection.getInstance(getContext()).getWasUnPaired()) {
                Toasty.info(mContext, "Unpaired with " + mBluetooth.getmPairedBluetoothDevice().getName(), Toast.LENGTH_LONG).show();
            } else {
                if (mBluetoothAdapter.isEnabled()){
                    Toasty.error(mContext, "Lost connection with " + mBluetooth.getmPairedBluetoothDevice().getName(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}