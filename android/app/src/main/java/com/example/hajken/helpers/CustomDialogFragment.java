package com.example.hajken.helpers;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.hajken.R;

public class CustomDialogFragment extends DialogFragment {

    private static final String TAG = "CustomDialogFragment";
    private TextView actionExecute;
    private TextView actionCancel;
    private TextView dialogHeading;
    private String heading, action;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            onAction = (OnActionInterface) getTargetFragment();
        } catch (ClassCastException exception) {
            Log.e(TAG, "onAttach: ClassCastException " + exception.getMessage());
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        //Inflates the dialog
        View view = inflater.inflate(R.layout.dialog_custom, container, false);

        dialogHeading = view.findViewById(R.id.heading);
        actionExecute = view.findViewById(R.id.action_execute);
        actionCancel = view.findViewById(R.id.action_cancel);

        //Event of cancel
        actionCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                onAction.controlVehicle(false);
                getDialog().dismiss();
            }
        });

        //Event of execute
        actionExecute.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                onAction.controlVehicle(true);
                getDialog().dismiss();
            }
        });

        //Changes the name of the heading of the dialog
        dialogHeading.setText(heading);

        //Changes the name of the action button of the dialog
        actionExecute.setText(action);
        return view;
    }

    public interface OnActionInterface {
        void controlVehicle(Boolean execute);
    }

    public OnActionInterface onAction; //interface object

    public void setDialogHeading(String incoming) {
        this.heading = incoming;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
