package com.example.hajken.fragments;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import com.example.hajken.InterfaceMainActivity;
import com.example.hajken.bluetooth.Bluetooth;
import com.example.hajken.bluetooth.BluetoothConnection;
import com.example.hajken.bluetooth.ConnectionListener;
import com.example.hajken.helpers.ListOfDevices;
import com.example.hajken.R;

import es.dmoral.toasty.Toasty;

public class ScanFragment extends Fragment implements View.OnClickListener, ConnectionListener {

    private static final String TAG = "ScanFragment";
    private Button scanButton, unPairButton, gateWayButton;
    private ListView mListView;
    private Context mContext;
    private ListOfDevices mListAdapter;
    private Bluetooth mBluetooth;
    private InterfaceMainActivity mInterfaceMainActivity;
    private BluetoothAdapter mBluetoothAdapter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
        mInterfaceMainActivity = (InterfaceMainActivity) getActivity();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        mBluetooth = Bluetooth.getInstance(mContext);
        mBluetoothAdapter = mBluetooth.getmBluetoothAdapter();

        super.onCreate(savedInstanceState);

        if (mBluetoothAdapter == null) {
            Toasty.error(mContext,getString(R.string.no_bluetooth_adapter),Toast.LENGTH_LONG).show();
        } else {
            if (!mBluetoothAdapter.isEnabled()) {
                Toasty.error(mContext,getString(R.string.no_active_bluetooth_adapter),Toast.LENGTH_LONG).show();
            } else {
                BluetoothConnection.getInstance(mContext).registerConnectionListener(this);
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_scan,container,false);

        scanButton = view.findViewById(R.id.scan_button);
        unPairButton = view.findViewById(R.id.unpair_button);
        gateWayButton = view.findViewById(R.id.gateway_button);
        mListView = view.findViewById(R.id.device_list);

        scanButton.setOnClickListener(this);
        gateWayButton.setOnClickListener(this);
        unPairButton.setOnClickListener(this);

        setStateOfButtons();

        return view;
    }

    public void setStateOfButtons(){
        gateWayButton.setClickable(true);
        gateWayButton.setActivated(true);

        if (mBluetooth.isConnected()){
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

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.scan_button:{
                mBluetooth.enableBluetooth();
                mBluetooth.getmBluetoothDevices().clear();
                mBluetooth.discover();
                enableListView();
                break;
            }
            case R.id.unpair_button:{
                mBluetooth.unPairDevice(mBluetooth.getmPairedBluetoothDevice());
                break;
            }
            case R.id.gateway_button:{
                mInterfaceMainActivity.inflateFragment(getString(R.string.gateway_fragment));
                break;
            }
        }
    }

    public void enableListView(){
        //Enables connect possibilities of bluetooth devices and displays devices
        mListAdapter = new ListOfDevices(getContext(), R.layout.listview_item, mBluetooth.getmBluetoothDevices());
        mBluetooth.setmListAdapter(mListAdapter);
        mListView.setAdapter(mBluetooth.getmListAdapter());
        mListView.setOnItemClickListener((adapterView, view, i, l) -> mBluetooth.bondWithDevice(getContext(), i));
    }

    @Override
    public void onConnect() {
        Toasty.success(mContext,getString(R.string.connected_text),Toast.LENGTH_LONG).show();
        setStateOfButtons();

    }

    @Override
    public void onNotConnected() {
        Toasty.error(mContext,getString(R.string.not_connected_text),Toast.LENGTH_LONG).show();
        setStateOfButtons();
    }


}