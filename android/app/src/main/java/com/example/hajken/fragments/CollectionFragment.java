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
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import com.example.hajken.helpers.CoordinateConverter;
import com.example.hajken.helpers.ListAdapter;
import com.example.hajken.helpers.OurData;
import com.example.hajken.helpers.RecyclerItemClickListener;
import com.example.hajken.bluetooth.BluetoothConnection;
import com.example.hajken.helpers.CustomDialogFragment;
import com.example.hajken.InterfaceMainActivity;
import com.example.hajken.R;
import java.util.ArrayList;

public class CollectionFragment extends Fragment implements View.OnClickListener, CustomDialogFragment.OnActionInterface {


    private static final String TAG = "CollectionFragment";
    private InterfaceMainActivity interfaceMainActivity;
    private RecyclerView recyclerView;
    private boolean vehicleOn = false;
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private OurData ourData = new OurData();
    private CoordinateConverter coordinateConverter;
    private SeekBar seekBar;
    private TextView amountOfLoops;

    //Data for the vehicle routes
    private final String circleRouteData = ""; // to be fixed
    private final String squareRouteData = "<F*30*R*90*F*30*R*90*F*30*R*90*F*30*R*90>";
    private String input;

    private final int SLOW = 1;
    private final int MED = 2;
    private final int FAST = 3;

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
                    BluetoothConnection.getInstance(getContext()).stopCar("s");  //<<<<----- here is the bluetooth activation/starting the vehicle
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
                    BluetoothConnection.getInstance(getContext()).startCar("g"); // <<<<----- here is the bluetooth activation/starting the vehicle
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
    }

    //occurs after onCreate
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //Inflates the collFragment
        final View view = inflater.inflate(R.layout.fragment_collection, container, false);

        //Speed changing
        radioGroup = view.findViewById(R.id.radio_group);
        amountOfLoops = view.findViewById(R.id.amount_of_repetitions);
        seekBar = view.findViewById(R.id.seekbar);

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

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);

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

        final ListAdapter listAdapter = new ListAdapter();
        recyclerView.setAdapter(listAdapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getContext(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Log.d(TAG, "position is: "+position);
                        if (BluetoothConnection.getInstance(getContext()).getIsConnected()) {

                            Toast.makeText(getActivity(), "Starting Car", Toast.LENGTH_SHORT).show();
                            ArrayList<PointF> makeToString = ourData.getCoordinates(position);
                            String instructions = coordinateConverter.returnInstructions(makeToString);
                            Log.d(TAG, "Instruction coordinates: " + instructions.toString());
                            BluetoothConnection.getInstance(getContext()).startCar(instructions);

                        } else {
                            Toast.makeText(getActivity(), "Not connected to a device", Toast.LENGTH_LONG).show();

                        }
                    }
                    @Override
                    public void onLongItemClick(View view, int position) {
                                            }
                }));
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
            case R.id.start_car_button: {

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
