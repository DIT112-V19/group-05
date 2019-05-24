package com.example.hajken;

//part of the collFragment to activity communication
//interface is used to handle messages/info that is sent between fragments
//interface is implemented in mainActivity

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;

public interface InterfaceMainActivity {

    //method to "start" new fragments
    void inflateFragment(String fragmentTag);

    Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter);

    void runOnUiThread(Runnable action);

    void setOnBackPressedActive(boolean onBackPressedActive);

}
