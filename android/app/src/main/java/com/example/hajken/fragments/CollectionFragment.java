package com.example.hajken.fragments;

import android.content.Context;
import android.graphics.PointF;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hajken.bluetooth.Bluetooth;
import com.example.hajken.helpers.CoordinateConverter;
import com.example.hajken.helpers.CoordinatesHolder;
import com.example.hajken.helpers.CoordinatesListItem;
import com.example.hajken.helpers.ListAdapter;
import com.example.hajken.bluetooth.BluetoothConnection;
import com.example.hajken.helpers.CustomDialogFragment;
import com.example.hajken.InterfaceMainActivity;
import com.example.hajken.R;
import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class CollectionFragment extends Fragment implements
        View.OnClickListener, CustomDialogFragment.OnActionInterface,
        BluetoothConnection.onBluetoothConnectionListener {


    private static final String TAG = "CollectionFragment";
    private InterfaceMainActivity mInterfaceMainActivity;
    private RecyclerView recyclerView;
    private boolean vehicleOn = false;
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private SeekBar seekBar;
    private ArrayList<PointF> validPoints;
    private TextView amountOfLoops;
    private TextView textView;
    private String instructions;
    private Button start_car_button;
    CustomDialogFragment dialog;
    private Bluetooth mBluetooth;


    //Data for the vehicle routes
    private final String circleRouteData = ""; // to be fixed
    private final String squareRouteData = "<F*30*R*90*F*30*R*90*F*30*R*90*F*30*R*90>";
    private String input;

    private final int SLOW = 1;
    private final int MED = 2;
    private final int FAST = 3;

    //calls before onCreate, used to instantiate the interface
    //part of the collFragment to activity communication
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mInterfaceMainActivity = (InterfaceMainActivity) getActivity();
        mBluetooth = Bluetooth.getInstance(getContext(), mInterfaceMainActivity);


    }

    //Changes the input to users choice
    public void setInput(String input) {
        this.input = input;
    }

    //Method checks indicates whether or not the vehicle is turned on (not connected to Bluetooth)
    public boolean isVehicleOn() {
        return vehicleOn;
    }


    @Override
    public void controlVehicle(Boolean execute) {
        Log.e(TAG, "controlVehicle: found incoming input");

        instructions = CoordinateConverter.getInstance(getContext()).returnInstructions(validPoints);

        //when vehicle is running
        if (isVehicleOn()){
            //when user chooses to stop the vehicle
            if (execute){
                if (instructions == null){
                    Toast.makeText(getActivity(),"Something went wrong 1",Toast.LENGTH_LONG).show();
                } else { // if there is route data
                    mBluetooth.stopCar("s");  //<<<<----- here is the bluetooth activation/starting the vehicle
                    vehicleOn = false;
                    Toast.makeText(getActivity(),"Vehicle stopping",Toast.LENGTH_LONG).show();
                }
            }

            //when vehicle is not running
        } else {
            //Change button state
            if (execute){
                if (instructions == null){
                    Toast.makeText(getActivity(),"Something went wrong 2",Toast.LENGTH_LONG).show();
                } else {
                    mBluetooth.startCar(instructions); // <<<<----- here is the bluetooth activation/starting the vehicle
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
        BluetoothConnection.getInstance(getContext()).registerListener(this);
        dialog = new CustomDialogFragment();


    }

    //occurs after onCreate
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //Inflates the collFragment
        final View view = inflater.inflate(R.layout.fragment_collection, container, false);

        //Speed changing
        radioGroup = view.findViewById(R.id.radio_group);
        amountOfLoops = view.findViewById(R.id.amount_of_repetitions);
        seekBar = view.findViewById(R.id.seekbar);
        start_car_button = view.findViewById(R.id.start_car_button);
        recyclerView = view.findViewById(R.id.recyclerView);
        start_car_button.setOnClickListener(this);
        start_car_button.setClickable(false);
        start_car_button.setActivated(false);



        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton checkedRadioButton = group.findViewById(checkedId);
                boolean isChecked = checkedRadioButton.isChecked();

                if (isChecked){
                    checkButton(view);
                }
            }
        });

        //Set amount of repetitions beginning at zero
        amountOfLoops.setText(getString(R.string.amount_of_repetitions,Integer.toString(0)));



        seekBar.setMax(10);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                CoordinateConverter.getInstance(getContext()).setNrOfLoops(progress);
                amountOfLoops.setText(getString(R.string.amount_of_repetitions,Integer.toString(progress)));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        final ListAdapter listAdapter = new ListAdapter(CoordinatesHolder.COORDINATES_LIST_ITEMS, new ListAdapter.onItemSelectedListener() {
            @Override
            public void onItemSelected(CoordinatesListItem coordinatesListItem) {

                if (BluetoothConnection.getInstance(getContext()).getIsConnected()){
                    Log.i(TAG, "onItemSelected: bitmap: " + coordinatesListItem.getmBitmap());
                    Log.d(TAG, "coordinateHandling: " + coordinatesListItem.getListOfCoordinates().toString() + " SIZE:" + coordinatesListItem.getListOfCoordinates().size());
                    validPoints = coordinatesListItem.getListOfCoordinates();
                    start_car_button.setClickable(true);
                    start_car_button.setActivated(true);
                } else {
                    Toast.makeText(getActivity(), "Not connected to a device", Toast.LENGTH_LONG).show();

                }


            }
        });
        recyclerView.setAdapter(listAdapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        return view;
    }



    public void checkButton(View view){
        int radioId = radioGroup.getCheckedRadioButtonId();
        radioButton = view.findViewById(radioId);

        switch (radioButton.getText().toString()){
            case "Slow" : {
                CoordinateConverter.getInstance(getContext()).setSpeed(SLOW);
                break;
            }

            case "Medium" : {
                CoordinateConverter.getInstance(getContext()).setSpeed(MED);
                break;
            }

            case "Fast" : {
                CoordinateConverter.getInstance(getContext()).setSpeed(FAST);
                break;
            }
        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            //These are the events that are associated with clicking of the buttons
            case R.id.start_car_button: {


                dialog.setDialogHeading("Are you ready?");
                dialog.setAction("Start");
                dialog.setTargetFragment(CollectionFragment.this,1);
                dialog.show(getFragmentManager(),"DIALOG");

                break;
            }
        }
    }

    @Override
    public void onConnect() {

        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    // previously used to display which device the phone was connected to, keep or throw away?
                   // textView.setText("Connected Device:" + BluetoothConnection.getInstance(getContext()).getDeviceName());
                }
            });
        }
    }

    @Override
    public void onNotConnected() {

        if(getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // previously used to display which device the phone was connected to, keep or throw away?

                    // textView.setText("Connected Device: None");
                }
            });
        }
    }

    @Override
    public void onCarRunning() {
        mInterfaceMainActivity.setOnBackPressedActive(false);

    }

    @Override
    public void onCarNotRunning() {
        mInterfaceMainActivity.setOnBackPressedActive(true);



    }


}
