package com.example.hajken;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

public class StartFragment extends Fragment implements View.OnClickListener{

    private static final String TAG = "StartFragment";
    private InterfaceMainActivity interfaceMainActivity;

    private Button ScanFragmentButton;
    private ImageView radar;

    //calls after onAttach
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    //occurs after onCreate
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_start,container,false);

        //Creates the buttons and image of the fragment
        ScanFragmentButton = view.findViewById(R.id.find_vehicle_button);
        radar = view.findViewById(R.id.radar_symbol);

        //Enables functions to buttons
        ScanFragmentButton.setOnClickListener(this); //"this" refers to the interface (View.OnClickListener)

        return view;
    }

    public void onClick(View view){
        //This is the events that are associated with the buttons
        interfaceMainActivity.inflateFragment(getString(R.string.scan_fragment));
    }

    //calls before onCreate, used to instantiate the interface
    //part of the fragment to activity communication
    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        interfaceMainActivity = (InterfaceMainActivity) getActivity();
    }
}











