package com.example.hajken;

//part of the collFragment to activity communication
//interface is used to handle messages/info that is sent between fragments
//interface is implemented in mainActivity

public interface InterfaceMainActivity {

    //method to "start" new fragments
    void inflateFragment(String fragmentTag);


}
