package com.example.hajken.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hajken.helpers.Bluetooth;
import com.example.hajken.helpers.BluetoothConnection;
import com.example.hajken.CustomDialogFragment;
import com.example.hajken.InterfaceMainActivity;
import com.example.hajken.R;

public class CollectionFragment extends Fragment implements View.OnClickListener, CustomDialogFragment.OnActionInterface {

    private static final String TAG = "CollectionFragment";
    private InterfaceMainActivity interfaceMainActivity;
    private ImageButton circle;
    private ImageButton square;
    private Button stopVehicleButton;
    private boolean vehicleOn = false;
    private Bluetooth mBluetooth = Bluetooth.getInstance();
    private BluetoothConnection mBluetoothConnection = BluetoothConnection.getInstance(getContext());
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
        if (isVehicleOn()) {
            //when user chooses to stop the vehicle
            if (execute) {
                if (input == null) {
                    Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_LONG).show();
                } else { // if there is route data
                    BluetoothConnection.getInstance(getContext()).stopCar("s");  //<<<<----- here is the bluetooth activation/starting the vehicle
                    circle.setClickable(true);
                    square.setClickable(true);
                    stopVehicleButton.setActivated(false);
                    stopVehicleButton.setClickable(false);
                    vehicleOn = false;
                    Toast.makeText(getActivity(), "Vehicle stopping", Toast.LENGTH_LONG).show();
                }
            }

            //when vehicle is not running
        } else {
            //Change button state
            if (execute) {
                if (input == null) {
                    Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_LONG).show();
                } else {
                    BluetoothConnection.getInstance(getContext()).startCar("g"); // <<<<----- here is the bluetooth activation/starting the vehicle
                    stopVehicleButton.setActivated(true);
                    stopVehicleButton.setOnClickListener(this);
                    circle.setClickable(false);
                    square.setClickable(false);
                    vehicleOn = true;
                    Toast.makeText(getActivity(), "Starting...", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    //occurs after onAttach
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    //occurs after onCreate
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //Inflates the collFragment
        View view = inflater.inflate(R.layout.fragment_collection, container, false);

        //Creates the buttons, listOfXCoordinates and image of the collFragment
        stopVehicleButton = view.findViewById(R.id.stop_vehicle_button);
        circle = view.findViewById(R.id.circle_symbol);
        square = view.findViewById(R.id.square_symbol);
        textView = view.findViewById(R.id.device_collectionFragment);
        if (BluetoothConnection.getInstance(getContext()).getIsConnected()){
            textView.setText("Connected Device:"+BluetoothConnection.getInstance(getContext()).getDeviceName());
        } else {
            textView.setText("Connected Device: None");
        }

        //Enables functions to buttons
        circle.setOnClickListener(this);
        square.setOnClickListener(this);

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

        switch (view.getId()) {

            //These are the events that are associated with clicking of the buttons
            case R.id.stop_vehicle_button: {

                dialog.setDialogHeading("Are you sure you want to stop the vehicle?");
                dialog.setAction("STOP");
                dialog.setTargetFragment(CollectionFragment.this, 1);
                dialog.show(getFragmentManager(), "DIALOG");
                Log.d(TAG, "onClick: Clicked Stop Vehicle");
                break;
            }

            case R.id.circle_symbol: {
                Log.d(TAG, "onClick: Clicked CIRCLE");
                if (BluetoothConnection.getInstance(getContext()).getIsConnected()) {
                    setInput(circleRouteData);
                    dialog.setAction("START");
                    dialog.setDialogHeading("Would you like to start the route?");
                    dialog.setTargetFragment(CollectionFragment.this, 1);
                    dialog.show(getFragmentManager(), "DIALOG");
                    break;
                } else {
                    Toast.makeText(getActivity(), "Not connected to a device", Toast.LENGTH_LONG).show();
                    break;
                }
            }

            case R.id.square_symbol: {
                Log.d(TAG, "onClick: Clicked SQUARE");
                if (BluetoothConnection.getInstance(getContext()).getIsConnected()) {
                    setInput(squareRouteData);
                    dialog.setAction("START");
                    dialog.setDialogHeading("Would you like to start the route?");
                    dialog.setTargetFragment(CollectionFragment.this, 1);
                    dialog.show(getFragmentManager(), "DIALOG");
                    break;
                } else {
                    Toast.makeText(getActivity(), "Not connected to a device", Toast.LENGTH_LONG).show();
                    break;
                }
            }

        }
    }
}
