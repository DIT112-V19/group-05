package com.example.hajken;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.hajken.bluetooth.Bluetooth;
import com.example.hajken.fragments.CollectionFragment;
import com.example.hajken.fragments.DrawFragment;
import com.example.hajken.fragments.GatewayFragment;
import com.example.hajken.fragments.GoogleMapsFragment;
import com.example.hajken.fragments.ScanFragment;
import com.example.hajken.fragments.StartFragment;

import es.dmoral.toasty.Toasty;

public class MainActivity extends AppCompatActivity implements InterfaceMainActivity{

    private static final String TAG = "MainActivity";
    private static MainActivity mMainActivity;
    private Bluetooth mBluetooth;
    private boolean isOnBackPressedActive;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);

        mMainActivity = this;
        this.registerReceiver(mReceiver,filter);
        isOnBackPressedActive = true;
        mBluetooth = Bluetooth.getInstance(this);

        init();
    }

    private void init(){
        StartFragment fragment = new StartFragment();
        doFragmentTransaction(fragment,getString(R.string.start_fragment),false);
    }

    private void doFragmentTransaction(Fragment fragment, String tag, boolean addToBackStack){

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_container,fragment,tag);

        if (addToBackStack){
            transaction.addToBackStack(tag);
        }
        transaction.commit();
    }


    //this method is responsible to start fragmentTransactions
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

        } else if (fragmentTag.equals(getString(R.string.google_maps_fragment))){

            GoogleMapsFragment fragment = new GoogleMapsFragment();
            doFragmentTransaction(fragment,fragmentTag,true);
        }

    }

    @Override
    public void onBackPressed(){
        if (isOnBackPressedActive){
            super.onBackPressed();
        } else {
            Log.d(TAG, "onBackPressed: cant go back");
        }
    }

    public BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            mBluetooth.actOnAction(action, intent);
        }
    };

    public void setOnBackPressedActive(boolean onBackPressedActive) {
        this.isOnBackPressedActive = onBackPressedActive;
    }

    @Override
    public void showToast(String message) {

        this.runOnUiThread(() -> {
            switch (message) {

                case "Starting" :
                    Toasty.info(getThis(),message,Toast.LENGTH_LONG).show();
                case "Completed route" :
                    Toasty.info(getThis(),message, Toast.LENGTH_LONG).show();
                case "Obstacle found" :
                    Toasty.info(getThis(),message, Toast.LENGTH_LONG).show();

            }
        });


    }

    public static MainActivity getThis(){
        return mMainActivity;
    }
}