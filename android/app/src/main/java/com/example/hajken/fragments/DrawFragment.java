package com.example.hajken.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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
    private final int MAX_SEEKBAR_LEVEL = 10;
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
    private CoordinateConverter mCoordinateConverter;
    private InterfaceMainActivity mInterfaceMainActivity;
    private SaveData mSaveData;
    private Vehicle mVehicle;
    private Context mContext;
    private FragmentManager mFragmentManager;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
        mInterfaceMainActivity = (InterfaceMainActivity) getActivity();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBluetooth = Bluetooth.getInstance(mContext);
        mSaveData = SaveData.getInstance(mContext);
        mVehicle = Vehicle.getInstance();
        mCustomDialog = new CustomDialogFragment();
        mFragmentManager = getFragmentManager();
        mCoordinateConverter = CoordinateConverter.getInstance(mContext);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_draw, container, false);

        sendToVehicleButton = view.findViewById(R.id.send_to_vehicle_button);
        canvasView = view.findViewById(R.id.canvasView);
        amountOfLoops = view.findViewById(R.id.amount_of_repetitions);
        seekBar = view.findViewById(R.id.seekbar);
        saveButton = view.findViewById(R.id.save_button);
        radioGroup = view.findViewById(R.id.radio_group);
        amountOfLoops.setText(getString(R.string.amount_of_repetitions, Integer.toString(ZERO)));

        sendToVehicleButton.setOnClickListener(this);
        sendToVehicleButton.setClickable(false);
        sendToVehicleButton.setActivated(false);

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton checkedRadioButton = group.findViewById(checkedId);
            boolean isChecked = checkedRadioButton.isChecked();

            if (isChecked) {
                checkButton(view);
            }
        });
        canvasView.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                sendToVehicleButton.setActivated(true);
                sendToVehicleButton.setClickable(true);
            }
            return false;
        });

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

        return view;
    }

    public void checkButton(View view) {
        int radioId = radioGroup.getCheckedRadioButtonId();
        radioButton = view.findViewById(radioId);

        switch (radioButton.getText().toString()) {
            case "Slow": {
                mCoordinateConverter.setSpeed(SLOW);
                break;
            }

            case "Medium": {
                mCoordinateConverter.setSpeed(MED);
                break;
            }

            case "High": {
                mCoordinateConverter.setSpeed(FAST);
                break;
            }
        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

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
                            mSaveData.savePNG(canvasView.getBitmap());
                            mSaveData.saveData(coordinatesListItem);
                        }
                        instructions = mCoordinateConverter.returnInstructions(canvasView.getValidPoints());
                        showStartDialog();
                    }
                    break;

                } else {
                    Toasty.error(mContext, getString(R.string.not_connected_text), Toast.LENGTH_LONG).show();
                    break;
                }
            }
        }
    }

    @Override
    public void controlVehicle(Boolean execute) {

        if (mVehicle.isRunning()) {
            if (execute) {
                mBluetooth.stopCar(getString(R.string.stop_vehicle_instruction));
            }
        } else {
            if (execute) {
                if (instructions == null) {
                    Toasty.error(mContext,getString(R.string.vehicle_malfunction_text),Toast.LENGTH_LONG).show();
                } else {
                    mBluetooth.startCar(instructions);
                }
            }
        }
    }

    public String createName() {
        return Integer.toString(SaveData.getInstance(getContext()).getList().size()) + getString(R.string.image_file_type);
    }

    public void showStartDialog(){
        mCustomDialog.setDialogHeading(getString(R.string.start_dialog_heading));
        mCustomDialog.setAction(getString(R.string.action_start_text));
        mCustomDialog.setTargetFragment(DrawFragment.this,1);
        mCustomDialog.show(mFragmentManager,getString(R.string.dialog_tag));
    }

    public void showStopDialog(){
        mCustomDialog.setDialogHeading(getString(R.string.start_dialog_heading));
        mCustomDialog.setAction(getString(R.string.action_stop_text));
        mCustomDialog.setTargetFragment(DrawFragment.this,1);
        mCustomDialog.show(mFragmentManager,getString(R.string.dialog_tag));
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

