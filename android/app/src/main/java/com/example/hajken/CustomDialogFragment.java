package com.example.hajken;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

//DialogFragment is similar to Fragment but it is shrunked
public class CustomDialogFragment extends DialogFragment {

    public interface OnActionInterface {
        void controlVehicle(Boolean start);
    }

    public OnActionInterface onAction; //instantiate interface object

    private static final String TAG = "CustomDialogFragment";
    private TextView actionOk;
    private TextView actionCancel;
    private TextView dialogHeading;
    private String heading,action = "Start";

    public void setDialogHeading(String incoming) {
        this.heading = incoming;
    }
    public void setAction(String action) {
        this.action = action;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        //Inflates the dialog
        View view = inflater.inflate(R.layout.dialog_custom,container,false);

        dialogHeading = view.findViewById(R.id.heading);
        actionOk = view.findViewById(R.id.action_ok);
        actionCancel = view.findViewById(R.id.action_cancel);

        //Event of cancel
        actionCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view){
                Log.d(TAG, "onCreateView: closing dialog");
                onAction.controlVehicle(false);
                getDialog().dismiss();
            }
        });

        //Event of start
        actionOk.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view){
                Log.d(TAG, "onCreateView: send to start");
                //Sends back to Collectionfragment -> controlVehicle is true
                onAction.controlVehicle(true);
                getDialog().dismiss();
            }
        });

        //Changes the name of the heading of the dialog
        dialogHeading.setText(heading);

        //Changes the name of the action button of the dialog
        actionOk.setText(action);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            onAction = (OnActionInterface) getTargetFragment();
        } catch (ClassCastException exception){
            Log.e(TAG, "onAttach: ClassCastException "+exception.getMessage());

        }

    }
}
