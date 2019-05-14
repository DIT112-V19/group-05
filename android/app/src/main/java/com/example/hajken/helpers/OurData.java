package com.example.hajken.helpers;

import android.content.Context;
import android.graphics.PointF;

import com.example.hajken.R;

import java.util.ArrayList;

public class OurData {

    private Context myContext;

    private static OurData mInstance = null;

    private OurData(Context context) {
        myContext = context;
    }

    public static OurData getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new OurData(context);
        }
        return mInstance;
    }

    public ArrayList<String> imageNames = new ArrayList<>();
    public ArrayList<Integer> images = new ArrayList<>();

    public void addToArrays(){
        imageNames.add("Square");
        imageNames.add("Circle");
        imageNames.add("Triangle");
        imageNames.add("Z");
        imageNames.add("Cat");

        images.add(R.drawable.square_symbol);
        images.add(R.drawable.circle_symbol);
        images.add(R.drawable.triangle_symbol);
        images.add(R.drawable.z_symbol);
        images.add(R.drawable.cat_symbol);

    }

    public static String[] imageName = new String[]{
            "Square",
            "Circle",
            "Triangle",
            "Z",
            "Cat"

    };

    public static int[] picturePath = new int[]{
            R.drawable.square_symbol,
            R.drawable.circle_symbol,
            R.drawable.triangle_symbol,
            R.drawable.z_symbol,
            R.drawable.cat_symbol


    };

    public static ArrayList<PointF> squareCoordinates = new ArrayList<PointF>(){{
        add(new PointF(0,0));
        add(new PointF(0,100));
        add(new PointF(100,100));
        add(new PointF(100,0));
        add(new PointF(0,0));
    }};

    public static ArrayList<PointF> circleCoordinates = new ArrayList<PointF>(){{
        add(new PointF(0,0));
        add(new PointF(0,50));
        add(new PointF(50,100));
        add(new PointF(150,100));
        add(new PointF(200,50));
        add(new PointF(200,-50));
        add(new PointF(150,-100));
        add(new PointF(50,-100));
        add(new PointF(0,-50));
        add(new PointF(0,0));
    }};



    public static ArrayList<ArrayList<PointF>> imageCoordinates = new ArrayList<ArrayList<PointF>>(){{
            add(squareCoordinates);
            add(circleCoordinates);
    }};

    public ArrayList<PointF> getCoordinates(int position){

            return imageCoordinates.get(position);



    }

    public ArrayList<ArrayList<PointF>> getImageCoordinates (){
        return imageCoordinates;
    }

}
