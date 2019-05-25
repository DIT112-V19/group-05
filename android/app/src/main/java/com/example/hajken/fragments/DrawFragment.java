package com.example.hajken.fragments;

import android.content.Context;
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
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import com.example.hajken.InterfaceMainActivity;
import com.example.hajken.bluetooth.Bluetooth;
import com.example.hajken.bluetooth.BluetoothConnection;
import com.example.hajken.helpers.CanvasView;
import com.example.hajken.helpers.CoordinatesListItem;
import com.example.hajken.helpers.CustomDialogFragment;
import com.example.hajken.helpers.CoordinateConverter;
import com.example.hajken.R;
import com.example.hajken.helpers.SaveData;
import com.example.hajken.helpers.Vehicle;

import es.dmoral.toasty.Toasty;

public class DrawFragment extends Fragment implements View.OnClickListener, CustomDialogFragment.OnActionInterface, BluetoothConnection.onBluetoothConnectionListener {

    private final int SLOW = 1;
    private final int MED = 2;
    private final int FAST = 3;
    private final int ZERO = 0;
    private static final String TAG = "DrawFragment";
    private Button sendToVehicleButton;
    private CanvasView canvasView;
    private String instructions;
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private CustomDialogFragment mCustomDialog;
    private TextView amountOfLoops;
    private SeekBar seekBar;
    private Bluetooth mBluetooth;
    private CheckBox saveButton;
    private InterfaceMainActivity mInterfaceMainActivity;
    private SaveData saveData;
    private Vehicle mVehicle;
    private Context mContext;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
        mInterfaceMainActivity = (InterfaceMainActivity) getActivity();
        mBluetooth = Bluetooth.getInstance(getContext());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCustomDialog = new CustomDialogFragment();
        saveData = SaveData.getInstance(getContext());
        mVehicle = Vehicle.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_draw, container, false);

        //Creates the buttons and canvasView
        sendToVehicleButton = view.findViewById(R.id.send_to_vehicle_button);
        canvasView = view.findViewById(R.id.canvasView);
        amountOfLoops = view.findViewById(R.id.amount_of_repetitions);
        seekBar = view.findViewById(R.id.seekbar);
        saveButton = view.findViewById(R.id.save_button);

        //Speed changing
        radioGroup = view.findViewById(R.id.radio_group);

        //Set amount of repetitions on inflation to zero
        amountOfLoops.setText(getString(R.string.amount_of_repetitions, Integer.toString(ZERO)));

        sendToVehicleButton.setOnClickListener(this);
        sendToVehicleButton.setClickable(false);
        sendToVehicleButton.setActivated(false);

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

        canvasView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    sendToVehicleButton.setActivated(true);
                    sendToVehicleButton.setClickable(true);
                }
                return false;
            }
        });

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

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            //This is the events that are associated with the buttons
            case R.id.send_to_vehicle_button: {

                if (mBluetooth.isConnected()) {
                    if (mVehicle.isRunning()){
                        showStopDialog();
                    } else {
                        if (saveButton.isChecked()) {
                            // create a java object to hold the bitmap with its respective coordinates
                            // will later be displayed in the recycler view

                            CoordinatesListItem coordinatesListItem = new CoordinatesListItem();
                            coordinatesListItem.setListOfCoordinates(canvasView.getValidPoints());
                            coordinatesListItem.setmName(createName());

                            SaveData.mItemList.add(coordinatesListItem);
                            Log.d(TAG, "drawfragment onclick bitmap " + canvasView.getBitmap());

                            saveData.savePNG(canvasView.getBitmap());
                            saveData.saveData(coordinatesListItem);
                        }
                        instructions = CoordinateConverter.getInstance(getContext()).returnInstructions(canvasView.getValidPoints());
                        showStartDialog();
                    }
                    break;

                } else {
                    Toasty.error(mContext, "Not connected", Toast.LENGTH_LONG).show();
                    break;
                }
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
                    Toasty.error(mContext,"Something went wrong",Toast.LENGTH_LONG).show();
                } else {
                    mBluetooth.startCar(instructions);
                }
            }
        }
    }

    public String createName() {
        return Integer.toString(SaveData.getInstance(getContext()).getList().size()) + ".png";
    }

    public void showStartDialog(){
        mCustomDialog.setDialogHeading("Would you like to start the vehicle?");
        mCustomDialog.setAction("Start");
        mCustomDialog.setTargetFragment(DrawFragment.this,1);
        mCustomDialog.show(getFragmentManager(),"DIALOG");
    }

    public void showStopDialog(){
        mCustomDialog.setDialogHeading("Would you like to stop the vehicle?");
        mCustomDialog.setAction("Stop");
        mCustomDialog.setTargetFragment(DrawFragment.this,1);
        mCustomDialog.show(getFragmentManager(),"DIALOG");
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
}

