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

    public interface OnStarted {
        void setUpStart(Boolean start);
    }

    public OnStarted onStarted; //instantiate interface object

    private static final String TAG = "CustomDialogFragment";
    private TextView actionOk, actionCancel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        //Inflates the dialog
        View view = inflater.inflate(R.layout.dialog_custom,container,false);

        actionOk = view.findViewById(R.id.action_ok);
        actionCancel = view.findViewById(R.id.action_cancel);

        //Event of cancel
        actionCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view){
                Log.d(TAG, "onCreateView: closing dialog");
                onStarted.setUpStart(false);
                getDialog().dismiss();
            }
        });

        //Event of start
        actionOk.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view){
                Log.d(TAG, "onCreateView: send to start");

                //Sends back to Collectionfragment -> setUpStart is true
                onStarted.setUpStart(true);
                getDialog().dismiss();
            }
        });
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            onStarted = (OnStarted) getTargetFragment();
        } catch (ClassCastException exception){
            Log.e(TAG, "onAttach: ClassCastException "+exception.getMessage());

        }

    }
}
