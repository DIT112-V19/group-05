package com.example.hajken;

import android.content.Context;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class DrawFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "DrawFragment";
    private InterfaceMainActivity interfaceMainActivity;

    private Button startCarButton;
    private Button clearButton;
    private CanvasView canvasView;
    private TextView textView;
    private MathUtility mathUtility;
    private CoordinateConverter coordinateConverter;
    private String instructions;

    //occurs after onAttach
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mathUtility = new MathUtility();
        coordinateConverter = new CoordinateConverter();
    }

    //occurs after onCreate
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_draw, container, false);

        //Creates the buttons and canvasView
        startCarButton = view.findViewById(R.id.start_car_button);
        clearButton = view.findViewById(R.id.clear_draw_button);
        canvasView = view.findViewById(R.id.canvasView);
        textView = view.findViewById(R.id.device_drawFragment);
        if (BluetoothConnection.getInstance(getContext()).getIsConnected()){
            textView.setText("Connected Device:"+BluetoothConnection.getInstance(getContext()).getDeviceName());
        } else {
            textView.setText("Connected Device: None");
        }




        startCarButton.setOnClickListener(this);
        clearButton.setOnClickListener(this);

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

            //This is the events that are associated with the buttons

            case R.id.start_car_button: {

                if (BluetoothConnection.getInstance(getContext()).getIsConnected()) {

                    Toast.makeText(getActivity(), "Starting Car", Toast.LENGTH_SHORT).show();
                    ArrayList<PointF> validPoints = mathUtility.findPoints2(canvasView.getListOfCoordinates(), 50.0);
                    Log.d(TAG, "coordinateHandling: " + validPoints.toString() + " SIZE:" + validPoints.size());
                    instructions = coordinateConverter.returnString(validPoints);
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
