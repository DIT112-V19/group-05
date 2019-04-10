package com.example.hajken;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

public class ScanFragment extends Fragment implements View.OnClickListener{

    //used to keep track of where we are in LogCat
    private static final String TAG = "ScanFragment";
    private InterfaceMainActivity interfaceMainActivity;

    //buttons and entities in fragment
    private Button scanButton, pairButton, unpairButton, routesButton;
    private ListView deviceList;
    private ImageView bluetoothSymbol;


    //onCreate is called before onCreateView
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_scan,container,false);

        //Creates the buttons, list and image of the fragment
        scanButton = view.findViewById(R.id.scan_button);
        pairButton = view.findViewById(R.id.pair_button);
        unpairButton = view.findViewById(R.id.unpair_button);
        routesButton = view.findViewById(R.id.routes_button);
        deviceList = view.findViewById(R.id.device_list);
        bluetoothSymbol = view.findViewById(R.id.bluetooth_symbol);

        //Enables functions to buttons
        routesButton.setOnClickListener(this);

        return view;
    }

    //onAttach is called before onCreate so that we instantiate the interface before using it
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        interfaceMainActivity = (InterfaceMainActivity) getActivity();
    }

    @Override
    public void onClick(View view) {

        //This is the events that are associated with the buttons
        switch (view.getId()){
            case R.id.scan_button:{

                break;
            }
            case R.id.pair_button:{

                break;
            }
            case R.id.unpair_button:{

                break;
            }
            case R.id.routes_button:{
                interfaceMainActivity.inflateFragment(getString(R.string.gateway_fragment));
                break;
            }
        }

    }
}


