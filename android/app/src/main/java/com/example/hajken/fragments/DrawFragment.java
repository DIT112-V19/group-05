package com.example.hajken.fragments;

import android.content.Context;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import com.example.hajken.bluetooth.BluetoothConnection;
import com.example.hajken.helpers.CanvasView;
import com.example.hajken.InterfaceMainActivity;
import com.example.hajken.helpers.CoordinatesHolder;
import com.example.hajken.helpers.CoordinatesListItem;
import com.example.hajken.helpers.CustomDialogFragment;
import com.example.hajken.helpers.CoordinateConverter;
import com.example.hajken.helpers.MathUtility;
import com.example.hajken.R;
import java.util.ArrayList;

public class DrawFragment extends Fragment implements View.OnClickListener, CustomDialogFragment.OnActionInterface{

    private final int SLOW = 1;
    private final int MED = 2;
    private final int FAST = 3;

    private static final String TAG = "DrawFragment";
    private InterfaceMainActivity interfaceMainActivity;

    private Button startCarButton;
    private CanvasView canvasView;
    private String instructions;
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private CustomDialogFragment mCustomDialogFragment;
    private boolean vehicleOn = false;
    private TextView amountOfLoops;
    private SeekBar seekBar;

    //occurs after onAttach
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCustomDialogFragment = new CustomDialogFragment();

    }

    //occurs after onCreate
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_draw, container, false);

        //Creates the buttons and canvasView
        startCarButton = view.findViewById(R.id.start_car_button);
        canvasView = view.findViewById(R.id.canvasView);
        amountOfLoops = view.findViewById(R.id.amount_of_repetitions);
        seekBar = view.findViewById(R.id.seekbar);

        //Speed changing
        radioGroup = view.findViewById(R.id.radio_group);

        //Set amount of repetitions beginning at zero
        amountOfLoops.setText(getString(R.string.amount_of_repetitions,Integer.toString(0)));

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

        canvasView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    startCarButton.setActivated(true);
                }

                return false;
            }
        });

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

        startCarButton.setOnClickListener(this);

        return view;
    }

    public void checkButton(View view) {
        int radioId = radioGroup.getCheckedRadioButtonId();
        radioButton = view.findViewById(radioId);

        switch (radioButton.getText().toString()) {
            case "Slow": {
                CoordinateConverter.getInstance(getContext()).setSpeed(SLOW);
                break;
            }

            case "Medium": {
                CoordinateConverter.getInstance(getContext()).setSpeed(MED);
                break;
            }

            case "High": {
                CoordinateConverter.getInstance(getContext()).setSpeed(FAST);
                break;
            }
        }
    }

    //calls before onCreate, used to instantiate the interface
    //part of the collFragment to activity communication
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        //used in case you would like to inflate new fragments from this fragment
        interfaceMainActivity = (InterfaceMainActivity) getActivity();
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            //This is the events that are associated with the buttons

            case R.id.start_car_button: {

                if (BluetoothConnection.getInstance(getContext()).getIsConnected()) {

                    // create a java object to hold the bitmap with its respective coordinates
                    // will later be displayed in the recycler view
                    CoordinatesListItem coordinatesListItem = new CoordinatesListItem();
                    coordinatesListItem.setListOfCoordinates(canvasView.getListOfCoordinates());
                    coordinatesListItem.setmBitmap(canvasView.getmBitmap());
                    Log.d(TAG, "drawfragment onclick bitmap " + canvasView.getmBitmap());

                    // added to a holder class which binds the bitmap together with its coordinates
                    CoordinatesHolder.COORDINATES_LIST_ITEMS.add(coordinatesListItem);
                    ArrayList<PointF> validPoints = MathUtility.getInstance(getContext()).rdpSimplifier(canvasView.getListOfCoordinates(), 65.0);
                    Log.d(TAG, "coordinateHandling: " + validPoints.toString() + " SIZE:" + validPoints.size());
                    instructions = CoordinateConverter.getInstance(getContext()).returnInstructions(validPoints);
                    Log.d(TAG, "Instruction coordinates: " + instructions.toString());
                    mCustomDialogFragment.setDialogHeading("Are you ready?");
                    mCustomDialogFragment.setAction("Start");
                    mCustomDialogFragment.setTargetFragment(DrawFragment.this,1);
                    mCustomDialogFragment.show(getFragmentManager(),"DIALOG");

                    break;

                } else {
                    Toast.makeText(getActivity(), "Not connected to a device", Toast.LENGTH_LONG).show();
                    break;

                }
            }
        }

    }

    @Override
    public void controlVehicle(Boolean execute) {
        Log.e(TAG, "controlVehicle: found incoming input");

        //when vehicle is running
        if (isVehicleOn()){
            //when user chooses to stop the vehicle
            if (execute){
                if (instructions == null){
                    Toast.makeText(getActivity(),"Something went wrong",Toast.LENGTH_LONG).show();
                } else { // if there is route data
                    BluetoothConnection.getInstance(getContext()).stopCar("s");  //<<<<----- here is the bluetooth activation/starting the vehicle
                    vehicleOn = false;
                    Toast.makeText(getActivity(),"Vehicle stopping",Toast.LENGTH_LONG).show();
                }
            }

            //when vehicle is not running
        } else {
            //Change button state
            if (execute){
                if (instructions == null){
                    Toast.makeText(getActivity(),"Something went wrong",Toast.LENGTH_LONG).show();
                } else {

                    BluetoothConnection.getInstance(getContext()).startCar(instructions);
                    Toast.makeText(getActivity(), "Starting Car", Toast.LENGTH_SHORT).show(); // <<<<----- here is the bluetooth activation/starting the vehicle
                    vehicleOn = true;
                    Toast.makeText(getActivity(),"Starting...",Toast.LENGTH_LONG).show();
                }
            }
        }
    }





    public boolean isVehicleOn() {
        return vehicleOn;
    }




}
