package com.example.hajken.fragments;

import android.content.Context;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.example.hajken.bluetooth.ConnectionListener;
import com.example.hajken.bluetooth.VehicleListener;
import com.example.hajken.helpers.CoordinateConverter;
import com.example.hajken.helpers.ListAdapter;
import com.example.hajken.bluetooth.BluetoothConnection;
import com.example.hajken.helpers.CustomDialogFragment;
import com.example.hajken.InterfaceMainActivity;
import com.example.hajken.R;
import com.example.hajken.helpers.SaveData;

import java.util.ArrayList;
import es.dmoral.toasty.Toasty;

public class CollectionFragment extends Fragment implements
        View.OnClickListener, CustomDialogFragment.OnActionInterface,
        ConnectionListener, VehicleListener {

    private final int SLOW = 1;
    private final int MED = 2;
    private final int FAST = 3;
    private final int ZERO = 0;
    private final int MAX_SEEKBAR_LEVEL = 10;
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
    private Context mContext;
    private SaveData mSaveData;
    private CoordinateConverter mCoordinateConverter;
    private FragmentManager mFragmentManager;
    private boolean isRunning;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
        mInterfaceMainActivity = (InterfaceMainActivity) getActivity();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBluetooth = Bluetooth.getInstance(getContext());
        mSaveData = SaveData.getInstance(mContext);
        mCoordinateConverter = CoordinateConverter.getInstance(mContext);
        mFragmentManager = getFragmentManager();
        mCustomDialog = new CustomDialogFragment();
        isRunning = false;
        BluetoothConnection.getInstance(mContext).registerVehicleListener(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_collection, container, false);

        radioGroup = view.findViewById(R.id.radio_group);
        amountOfLoops = view.findViewById(R.id.amount_of_repetitions);
        seekBar = view.findViewById(R.id.seekbar);
        sendToVehicleButton = view.findViewById(R.id.send_to_vehicle_button);

        sendToVehicleButton.setOnClickListener(this);
        sendToVehicleButton.setClickable(false);
        sendToVehicleButton.setActivated(false);

        recyclerView = view.findViewById(R.id.recyclerView);
        mSaveData.loadData();

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton checkedRadioButton = group.findViewById(checkedId);
            boolean isChecked = checkedRadioButton.isChecked();

            if (isChecked){
                checkButton(view);
            }
        });
        amountOfLoops.setText(getString(R.string.amount_of_repetitions,Integer.toString(ZERO)));
        seekBar.setMax(MAX_SEEKBAR_LEVEL);

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

        final ListAdapter listAdapter = new ListAdapter(SaveData.getInstance(getContext()).getList(), coordinatesListItem -> {
            if (mBluetooth.isConnected()){ validPoints = coordinatesListItem.getListOfCoordinates();
                sendToVehicleButton.setClickable(true);
                sendToVehicleButton.setActivated(true);
            } else {
                Toasty.error(mContext,getString(R.string.not_connected_text),Toast.LENGTH_LONG).show();

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

        switch (radioButton.getText().toString()){
            case "Slow" : {
                mCoordinateConverter.setSpeed(SLOW);
                break;
            }

            case "Medium" : {
                mCoordinateConverter.setSpeed(MED);
                break;
            }

            case "Fast" : {
                mCoordinateConverter.setSpeed(FAST);
                break;
            }
        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.send_to_vehicle_button: {

                    if (isRunning) {
                        showStopDialog();
                    } else {
                        instructions = mCoordinateConverter.returnInstructions(validPoints);
                        showStartDialog();
                    }
                break;
            }
        }
    }

    @Override
    public void controlVehicle(Boolean execute) {

        if (isRunning){
            if (execute){
                mBluetooth.stopCar(getString(R.string.stop_vehicle_instruction));
            }
        } else {
            if (execute){
                if (instructions == null){
                    Toasty.error(mContext,getString(R.string.vehicle_malfunction_text),Toast.LENGTH_LONG).show();
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
        Toasty.error(mContext,getString(R.string.not_connected_text),Toast.LENGTH_LONG).show();
    }

    @Override
    public void onCarRunning() {
        isRunning = true;
        Toasty.info(mContext,"Starting", Toast.LENGTH_LONG).show();
        sendToVehicleButton.setText(getString(R.string.stop_vehicle_text));
        mInterfaceMainActivity.setOnBackPressedActive(false);
    }

    @Override
    public void onCarNotRunning() {
        isRunning = false;
        Toasty.info(mContext,"Completed route", Toast.LENGTH_LONG).show();
        sendToVehicleButton.setText(getString(R.string.start_vehicle_text));
        mInterfaceMainActivity.setOnBackPressedActive(true);
    }

    @Override
    public void onFoundObstacle() {
        Toasty.info(mContext,"Obstacle found", Toast.LENGTH_LONG).show();


    }

    public void showStartDialog(){
        mCustomDialog.setDialogHeading(getString(R.string.start_dialog_heading));
        mCustomDialog.setAction(getString(R.string.action_start_text));
        mCustomDialog.setTargetFragment(CollectionFragment.this,1);
        mCustomDialog.show(mFragmentManager,getString(R.string.dialog_tag));
    }

    public void showStopDialog(){
        mCustomDialog.setDialogHeading(getString(R.string.stop_dialog_heading));
        mCustomDialog.setAction(getString(R.string.action_start_text));
        mCustomDialog.setTargetFragment(CollectionFragment.this,1);
        mCustomDialog.show(mFragmentManager,getString(R.string.dialog_tag));
    }
}
