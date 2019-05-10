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
import android.widget.TextView;
import android.widget.Toast;

import com.example.hajken.bluetooth.BluetoothConnection;
import com.example.hajken.helpers.CanvasView;
import com.example.hajken.InterfaceMainActivity;
import com.example.hajken.helpers.MathUtility;
import com.example.hajken.R;

import java.util.ArrayList;

public class DrawFragment extends Fragment implements View.OnClickListener {

    private final int LOW = 1;
    private final int MED = 2;
    private final int HIGH = 3;

    private static final String TAG = "DrawFragment";
    private InterfaceMainActivity interfaceMainActivity;

    private Button startCarButton;
    private Button clearButton;
    private CanvasView canvasView;
    private TextView textView;
    private String instructions;
    private RadioGroup radioGroup;
    private RadioButton radioButton;

    //occurs after onAttach
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    //occurs after onCreate
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_draw, container, false);

        //Creates the buttons and canvasView
        startCarButton = view.findViewById(R.id.start_car_button);
        clearButton = view.findViewById(R.id.clear_draw_button);
        canvasView = view.findViewById(R.id.canvasView);
        textView = view.findViewById(R.id.device_drawFragment);

        //Speed changing
        radioGroup = view.findViewById(R.id.radiogroup2);

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

        if (BluetoothConnection.getInstance(getContext()).getIsConnected()) {
            textView.setText("Connected Device:" + BluetoothConnection.getInstance(getContext()).getDeviceName());
        } else {
            textView.setText("Connected Device: None");
        }

        canvasView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    startCarButton.setActivated(true);
                }

                return false;
            }
        });

        startCarButton.setOnClickListener(this);
        clearButton.setOnClickListener(this);

        return view;
    }

    public void checkButton(View view) {
        int radioId = radioGroup.getCheckedRadioButtonId();
        radioButton = view.findViewById(radioId);

        switch (radioButton.getText().toString()) {
            case "Low": {
                CoordinateConverter.getInstance(getContext()).setSpeed(LOW);
                break;
            }

            case "Medium": {
                CoordinateConverter.getInstance(getContext()).setSpeed(MED);
                break;
            }

            case "High": {
                CoordinateConverter.getInstance(getContext()).setSpeed(HIGH);
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

                    Toast.makeText(getActivity(), "Starting Car", Toast.LENGTH_SHORT).show();
                    ArrayList<PointF> validPoints = MathUtility.getInstance(getContext()).rdpSimplifier(canvasView.getListOfCoordinates(), 65.0);
                    Log.d(TAG, "coordinateHandling: " + validPoints.toString() + " SIZE:" + validPoints.size());
                    instructions = CoordinateConverter.getInstance(getContext()).returnString(validPoints);
                    Log.d(TAG, "Instruction coordinates: " + instructions.toString());
                    BluetoothConnection.getInstance(getContext()).startCar(instructions);
                    break;

                } else {
                    Toast.makeText(getActivity(), "Not connected to a device", Toast.LENGTH_LONG).show();
                    break;

                }
            }
            case R.id.clear_draw_button: {
                canvasView.clearCanvas();

            }
        }

    }
}
