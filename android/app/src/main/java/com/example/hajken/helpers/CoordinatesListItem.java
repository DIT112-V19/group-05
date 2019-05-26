package com.example.hajken.helpers;

import android.graphics.Bitmap;
import android.graphics.PointF;

import java.io.File;
import java.util.ArrayList;

public class CoordinatesListItem {

    private String mName;
    private ArrayList<PointF> listOfCoordinates = new ArrayList<>();


    public ArrayList<PointF> getListOfCoordinates() {
        return listOfCoordinates;
    }

    public void setListOfCoordinates(ArrayList<PointF> listOfCoordinates) {
        this.listOfCoordinates = listOfCoordinates;
    }

    public void setmName(String path) {
        this.mName = path;
    }

    String getmName() {
        return this.mName;
    }
}
