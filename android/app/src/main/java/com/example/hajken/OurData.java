package com.example.hajken;

import android.graphics.PointF;

import java.util.ArrayList;

public class OurData {

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
