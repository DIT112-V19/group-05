package com.example.hajken;

import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements InterfaceMainActivity {

    private static final String TAG = "MainActivity";
    private boolean vehicleActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init(); // when mainActivity starts, it will inflate StartFragment first
    }

    private void init(){
        StartFragment fragment = new StartFragment();
        doFragmentTransaction(fragment,getString(R.string.start_fragment),false);
    }


    //used to be a string message here as well but we are not using that
    private void doFragmentTransaction(Fragment fragment, String tag, boolean addToBackStack){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.replace(R.id.main_container,fragment,tag);

        if (addToBackStack){
            transaction.addToBackStack(tag);
        }
        transaction.commit();
    }

    //this method is responsible to inflate fragments
    @Override
    public void inflateFragment(String fragmentTag) {

        if (fragmentTag.equals(getString(R.string.scan_fragment))){

            ScanFragment fragment = new ScanFragment();
            doFragmentTransaction(fragment, fragmentTag, true); //addToBackStack(true) makes it possible to go back without closing the app

        } else if (fragmentTag.equals(getString(R.string.gateway_fragment))){

            GatewayFragment fragment = new GatewayFragment();
            doFragmentTransaction(fragment,fragmentTag,true); //addToBackStack(true) makes it possible to go back without closing the app

        } else if (fragmentTag.equals(getString(R.string.draw_fragment))){

            DrawFragment fragment = new DrawFragment();
            doFragmentTransaction(fragment,fragmentTag,true); //addToBackStack(true) makes it possible to go back without closing the app

        } else if (fragmentTag.equals(getString(R.string.collection_fragment))){

            CollectionFragment fragment = new CollectionFragment();
            doFragmentTransaction(fragment,fragmentTag,true); //addToBackStack(true) makes it possible to go back without closing the app

        }

    }

    private void cancelMethod(){
        Log.d(TAG, "cancelMethod: Called");
        vehicleActivity = false;
        toastMessage("Route cancelled");
    }

    private void startMethod(){
        Log.d(TAG, "startMethod: Called");
        vehicleActivity = true;
        toastMessage("Route started");

    }

    @Override
    public void customDialog(String title, String message, final String cancelMethod, final String startMethod) {
        Log.d(TAG, "customDialog: Called");
        final android.support.v7.app.AlertDialog.Builder builderSingle = new android.support.v7.app.AlertDialog.Builder(this); // "this" references to interface

        builderSingle.setTitle(title);
        builderSingle.setMessage(message);

        builderSingle.setNegativeButton(
                "Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                            Log.d(TAG, "onClick: Called");
                            if (cancelMethod.equals("cancelMethod")){
                                cancelMethod();
                            }
                        }
                    });
        builderSingle.setPositiveButton(
                "Start",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "onClick: Called");
                        if (startMethod.equals("startMethod")){
                            startMethod();
                        }
                    }
                }
        );

        builderSingle.show();

    }

    @Override
    public boolean getVehicleActivity() {
        Log.d(TAG, "getVehicleActivity: Called");
        return this.vehicleActivity;
    }

    private void toastMessage(String message){
        Log.d(TAG, "toastMessage: Called");
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }
}