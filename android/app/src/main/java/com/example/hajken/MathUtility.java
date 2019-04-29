package com.example.hajken;

import android.graphics.PointF;
import android.util.Log;

import java.util.ArrayList;

public class MathUtility {

    private static final float DIFFERENCE_MARGIN = 30;

    private static final String TAG = "MathUtility";

    public ArrayList<PointF> findPoints(ArrayList<PointF> listOfCoordinates){

        Log.d(TAG, "incomingPoints: "+listOfCoordinates.toString()+" SIZE"+listOfCoordinates.size());
        ArrayList<PointF> validPoints = new ArrayList<>();
        PointF startPoint;
        PointF firstPoint;
        PointF secondPoint;
        PointF thirdPoint;

        //if there is only one or less points in list
        if (listOfCoordinates.size() <= 1){
            return null;
        }

        //gets the starting point and stores the starting point among the valid points
        startPoint = listOfCoordinates.get(0);
        Log.d(TAG, "ADD START: "+startPoint.toString());
        validPoints.add(startPoint);


        //gets the first point after starting point
        firstPoint = startPoint;
        secondPoint = listOfCoordinates.get(1);

        for (int i = 2; i < listOfCoordinates.size()-1;i++) {
            thirdPoint = listOfCoordinates.get(i);

            Log.d(TAG, "findPoints: "+"first:"+firstPoint+" second:"+secondPoint+" third:"+thirdPoint);

            if (isNewVector(firstPoint,secondPoint,thirdPoint)){
                Log.d(TAG, "findPoints: Is new Vector true");
                Log.d(TAG, "ADDING: "+secondPoint.toString());
                validPoints.add(secondPoint);
                firstPoint = secondPoint;
                secondPoint = thirdPoint;
            } else {
                Log.d(TAG, "findPoints: Is new Vector false");
                firstPoint = secondPoint;
                secondPoint = thirdPoint;
            }

        }

        //add last point in list to valid points
        validPoints.add(secondPoint);
        Log.d(TAG, "ADD LAST: "+secondPoint.toString());

        Log.d(TAG, "findpoints vector list: "+validPoints.toString());
        return validPoints;
    }

    public boolean isNewVector(PointF firstPoint, PointF secondPoint, PointF thirdPoint){

        Log.d(TAG, "isNewVector: "+thirdPoint.toString());
        Log.d(TAG, "isNewVector: "+secondPoint.toString());
        Log.d(TAG, "isNewVector: "+firstPoint.toString());


        double a1 = thirdPoint.y - secondPoint.y;
        Log.d(TAG, "vector diff Y third-second: "+a1);
        double b1 = thirdPoint.x - secondPoint.x;
        Log.d(TAG, "vector diff X third-second: "+b1);


        double a2 = secondPoint.y - firstPoint.y;
        Log.d(TAG, "vector diff y second-first: "+a2);

        double b2 = secondPoint.x - firstPoint.x;
        Log.d(TAG, "vector diff x second-first: "+b2);

        if (a1 == 0.0 || b1 == 0.0|| a2 == 0.0 || b2 == 0.0) {
            return false;
        }

        Log.d(TAG, "isNewVector: grades second to third"+Math.toDegrees(Math.atan2(a1,b1)));
        Log.d(TAG, "isNewVector: grades first to second"+Math.toDegrees(Math.atan2(a2,b2)));


        Log.d(TAG, "isNewVector: grades:"+(Math.toDegrees(Math.atan2(a1,b1))-Math.toDegrees(Math.atan2(a2,b2))));

        return Math.abs((Math.toDegrees(Math.atan2(a1,b1)))-(Math.toDegrees(Math.atan2(a2,b2)))) > DIFFERENCE_MARGIN;
    }

    public float getMagnitude(PointF pointA, PointF pointB){
        return (float) Math.sqrt(Math.pow((pointB.x-pointA.x),2)+Math.pow((pointB.y-pointA.y),2));
    }

}
