package com.example.hajken;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class CollectionFragment extends Fragment implements View.OnClickListener, CustomDialogFragment.OnActionInterface  {

    private static final String TAG = "CollectionFragment";
    private InterfaceMainActivity interfaceMainActivity;
    private Button stopVehicleButton;
    private RecyclerView recyclerView;
    private boolean vehicleOn = false;
    private Bluetooth mBluetooth = Bluetooth.getInstance();
    private BluetoothConnection mBluetoothConnection;
    private TextView textView;

    //Data for the vehicle routes
    private final String circleRouteData = ""; // to be fixed
    private final String squareRouteData = "<F*30*R*90*F*30*R*90*F*30*R*90*F*30*R*90>";
    private String input;

    //Changes the input to users choice
    public void setInput(String input) {
        this.input = input;
    }

    //Method checks indicates whether or not the vehicle is turned on (not connected to Bluetooth)
    public boolean isVehicleOn() {
        return vehicleOn;
    }

    CustomDialogFragment dialog = new CustomDialogFragment();

    @Override
    public void controlVehicle(Boolean execute) {
        Log.e(TAG, "controlVehicle: found incoming input");



        //when vehicle is running
        if (isVehicleOn()){
            //when user chooses to stop the vehicle
            if (execute){
                if (input == null){
                    Toast.makeText(getActivity(),"Something went wrong",Toast.LENGTH_LONG).show();
                } else { // if there is route data
                    mBluetoothConnection.getInstance(getContext()).stopCar("s");  //<<<<----- here is the bluetooth activation/starting the vehicle
                    vehicleOn = false;
                    Toast.makeText(getActivity(),"Vehicle stopping",Toast.LENGTH_LONG).show();
                }
            }

            //when vehicle is not running
        } else {
            //Change button state
            if (execute){
                if (input == null){
                    Toast.makeText(getActivity(),"Something went wrong",Toast.LENGTH_LONG).show();
                } else {
                    mBluetoothConnection.getInstance(getContext()).startCar("g"); // <<<<----- here is the bluetooth activation/starting the vehicle
                    vehicleOn = true;
                    Toast.makeText(getActivity(),"Starting...",Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    //occurs after onAttach
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBluetoothConnection = BluetoothConnection.getInstance(getContext());

    }

    //occurs after onCreate
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //Inflates the collFragment
        View view = inflater.inflate(R.layout.fragment_collection,container,false);

        //Creates the buttons, listOfXCoordinates and image of the collFragment
        stopVehicleButton = view.findViewById(R.id.stop_vehicle_button);
        textView = view.findViewById(R.id.device_collectionFragment);


        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);

        ListAdapter listAdapter = new ListAdapter();
        recyclerView.setAdapter(listAdapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        if (BluetoothConnection.getInstance(getContext()).getIsConnected()){
            textView.setText("Connected Device:"+BluetoothConnection.getInstance(getContext()).getDeviceName());
        } else {
            textView.setText("Connected Device: None");
        }



        return view;
    }

    //calls before onCreate, used to instantiate the interface
    //part of the collFragment to activity communication
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        interfaceMainActivity = (InterfaceMainActivity) getActivity();


    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            //These are the events that are associated with clicking of the buttons
            case R.id.stop_vehicle_button: {

                dialog.setDialogHeading("Are you sure you want to stop the vehicle?");
                dialog.setAction("STOP");
                dialog.setTargetFragment(CollectionFragment.this,1);
                dialog.show(getFragmentManager(),"DIALOG");
                Log.d(TAG, "onClick: Clicked Stop Vehicle");
                break;
            }





        }
    }
}
