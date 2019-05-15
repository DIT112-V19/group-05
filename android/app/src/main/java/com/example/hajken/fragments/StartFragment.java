package com.example.hajken.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.example.hajken.InterfaceMainActivity;
import com.example.hajken.R;

public class StartFragment extends Fragment implements View.OnClickListener{

    private static final String TAG = "StartFragment";
    private InterfaceMainActivity interfaceMainActivity;
    private Button scanFragmentButton;

    @Override
    public void onAttach(Context context){
        Log.d(TAG, "TAG STARTFRAGMENT - onAttach");

        super.onAttach(context);
        interfaceMainActivity = (InterfaceMainActivity) getActivity();
    }

    public void onCreate(@Nullable Bundle savedInstanceState){
        Log.d(TAG, "TAG STARTFRAGMENT - onCreate");

        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "TAG STARTFRAGMENT - onCreateView");

        //Inflates this fragment
        View view = inflater.inflate(R.layout.fragment_start,container,false);

        //Creates the buttons and enables functions
        scanFragmentButton = view.findViewById(R.id.find_vehicle_button);
        scanFragmentButton.setActivated(true);
        scanFragmentButton.setOnClickListener(this);

        return view;
    }

    public void onClick(View view) {
        Log.d(TAG, "TAG STARTFRAGMENT - onClick");


        switch (view.getId()) {

            //These are the events that are associated with clicking of the buttons
            case R.id.find_vehicle_button: {

                //Inflates -> Scan fragment
                interfaceMainActivity.inflateFragment(getString(R.string.scan_fragment));
            }
        }
    }
}