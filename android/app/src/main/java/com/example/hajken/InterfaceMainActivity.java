package com.example.hajken;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;

public interface InterfaceMainActivity {

    void inflateFragment(String fragmentTag);

    Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter);

    void setOnBackPressedActive(boolean onBackPressedActive);

}
