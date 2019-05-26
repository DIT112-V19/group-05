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

import com.example.hajken.bluetooth.Bluetooth;
import com.example.hajken.helpers.CoordinateConverter;
import com.example.hajken.helpers.CoordinatesListItem;
import com.example.hajken.helpers.ListAdapter;
import com.example.hajken.bluetooth.BluetoothConnection;
import com.example.hajken.helpers.CustomDialogFragment;
import com.example.hajken.InterfaceMainActivity;
import com.example.hajken.R;
import com.example.hajken.helpers.SaveData;
import com.example.hajken.helpers.Vehicle;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

public class CollectionFragment extends Fragment implements
        View.OnClickListener, CustomDialogFragment.OnActionInterface,
        BluetoothConnection.onBluetoothConnectionListener {

    private static final String TAG = "CollectionFragment";
    private InterfaceMainActivity mInterfaceMainActivity;
    private RecyclerView recyclerView;
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private SeekBar seekBar;
    private ArrayList<PointF> validPoints;
    private TextView amountOfLoops;
    private String instructions;
    private Button sendToVehicleButton;
    private CustomDialogFragment mCustomDialog;
    private Bluetooth mBluetooth;
    private final int SLOW = 1;
    private final int MED = 2;
    private final int FAST = 3;
    private Vehicle mVehicle;
    private Context mContext;

    //calls before onCreate, used to instantiate the interface
    //part of the collFragment to activity communication
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
        mInterfaceMainActivity = (InterfaceMainActivity) getActivity();
        mBluetooth = Bluetooth.getInstance(getContext());
    }

    //occurs after onAttach
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //BluetoothConnection.getInstance(getContext()).registerListener(this);
        mCustomDialog = new CustomDialogFragment();
        mVehicle = Vehicle.getInstance();

    }

    //occurs after onCreate
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //Inflates the collFragment
        final View view = inflater.inflate(R.layout.fragment_collection, container, false);

        radioGroup = view.findViewById(R.id.radio_group);
        amountOfLoops = view.findViewById(R.id.amount_of_repetitions);
        seekBar = view.findViewById(R.id.seekbar);
        sendToVehicleButton = view.findViewById(R.id.send_to_vehicle_button);

        sendToVehicleButton.setOnClickListener(this);
        sendToVehicleButton.setClickable(false);
        sendToVehicleButton.setActivated(false);

        recyclerView = view.findViewById(R.id.recyclerView);

        SaveData.getInstance(getContext()).loadData();

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton checkedRadioButton = group.findViewById(checkedId);
                boolean isChecked = checkedRadioButton.isChecked();

                if (isChecked) {
                    checkButton(view);
                }
            }
        });

        //Set amount of repetitions beginning at zero
        amountOfLoops.setText(getString(R.string.amount_of_repetitions, Integer.toString(0)));
        seekBar.setMax(10);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                CoordinateConverter.getInstance(getContext()).setNrOfLoops(progress);
                amountOfLoops.setText(getString(R.string.amount_of_repetitions, Integer.toString(progress)));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        final ListAdapter listAdapter = new ListAdapter(SaveData.getInstance(getContext()).getList(), new ListAdapter.onItemSelectedListener() {
            @Override
            public void onItemSelected(CoordinatesListItem coordinatesListItem) {

                if (mBluetooth.isConnected()) {
                    Log.d(TAG, "coordinateHandling: " + coordinatesListItem.getListOfCoordinates().toString() + " SIZE:" + coordinatesListItem.getListOfCoordinates().size());
                    validPoints = coordinatesListItem.getListOfCoordinates();
                    sendToVehicleButton.setClickable(true);
                    sendToVehicleButton.setActivated(true);
                } else {
                    Toasty.error(mContext, "Not connected to a device", Toast.LENGTH_LONG).show();
                }
            }
        });
        recyclerView.setAdapter(listAdapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
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

            case "Fast": {
                CoordinateConverter.getInstance(getContext()).setSpeed(FAST);
                break;
            }
        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            //These are the events that are associated with clicking of the buttons
            case R.id.send_to_vehicle_button: {

                if (mBluetooth.isConnected()) {
                    if (mVehicle.isRunning()) {
                        showStopDialog();
                    } else {
                        instructions = CoordinateConverter.getInstance(getContext()).returnInstructions(validPoints);
                        showStartDialog();
                    }
                } else {
                    Toasty.error(mContext, "Not connected", Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }

    @Override
    public void controlVehicle(Boolean execute) {

        //when vehicle is running
        if (mVehicle.isRunning()) {
            //when user chooses to stop the vehicle
            if (execute) {
                mBluetooth.stopCar("s");
            }

            //when vehicle is not running
        } else {
            //Change button state
            if (execute) {
                if (instructions == null) {
                    Toasty.error(mContext, "Something went wrong", Toast.LENGTH_LONG).show();
                } else {
                    mBluetooth.startCar(instructions);
                }
            }
        }
    }

    @Override
    public void onConnect() {

    }

    @Override
    public void onNotConnected() {
        sendToVehicleButton.setActivated(false);
        sendToVehicleButton.setClickable(false);
    }

    @Override
    public void onCarRunning() {
        sendToVehicleButton.setText(getString(R.string.stop_vehicle_text));
        mInterfaceMainActivity.setOnBackPressedActive(false);
    }

    @Override
    public void onCarNotRunning() {
        sendToVehicleButton.setText(getString(R.string.start_vehicle_text));
        mInterfaceMainActivity.setOnBackPressedActive(true);
    }

    @Override
    public void onFoundObstacle() {

    }

    public void showStartDialog() {
        mCustomDialog.setDialogHeading("Would you like to start the vehicle?");
        mCustomDialog.setAction("Start");
        mCustomDialog.setTargetFragment(CollectionFragment.this, 1);
        mCustomDialog.show(getFragmentManager(), "DIALOG");
    }

    public void showStopDialog() {
        mCustomDialog.setDialogHeading("Would you like to stop the vehicle?");
        mCustomDialog.setAction("Stop");
        mCustomDialog.setTargetFragment(CollectionFragment.this, 1);
        mCustomDialog.show(getFragmentManager(), "DIALOG");
    }
}
