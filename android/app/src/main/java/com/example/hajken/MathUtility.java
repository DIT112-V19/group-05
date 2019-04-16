package com.example.hajken;

import android.graphics.PointF;
import android.util.Log;

import java.util.ArrayList;

public class MathUtility {

    private static final float ANGLE_MARGIN = 20;

    private static final String TAG = "MathUtility";

    public ArrayList<PointF> findPoints(ArrayList<PointF> listOfCoordinates){

        Log.d(TAG, "incomingPoints: "+listOfCoordinates.toString()+" SIZE"+listOfCoordinates.size());

        ArrayList<PointF> validPoints = new ArrayList<>();

        //if there is only one or less points in list
        if (listOfCoordinates.size() <= 1){
            return null;
        }

        //gets the starting point and stores the starting point among the valid points
        PointF startPoint = listOfCoordinates.get(0);
        Log.d(TAG, "ADD START: "+startPoint.toString());
        validPoints.add(startPoint);

        //gets the first point after starting point
        PointF previousPoint = listOfCoordinates.get(1);
        Log.d(TAG, "previousPoint: "+previousPoint.toString());
        PointF nextPoint;

        for (int i = 2; i < listOfCoordinates.size();i++) {
            nextPoint = listOfCoordinates.get(i);
            Log.d(TAG, "nextPoint: "+nextPoint.toString());

            if (isValidAngle(previousPoint,nextPoint)){
                validPoints.add(previousPoint);
                Log.d(TAG, "ADD: "+previousPoint.toString());

            }
            previousPoint = nextPoint;
            Log.d(TAG, "previousPoint: "+previousPoint.toString());

        }

        //add last point in list to valid points
        validPoints.add(previousPoint);
        Log.d(TAG, "ADD LAST: "+previousPoint.toString());
        return validPoints;
    }

    public boolean isValidAngle(PointF previousPoint, PointF nextPoint){

        //Math.abs since there might be negative angles that are valid
        if (Math.abs(getAngle(previousPoint,nextPoint)) >= ANGLE_MARGIN){
            return true;
        } else {
            return false;
        }
    }

    public float getAngle(PointF previousPoint, PointF nextPoint) {

        float xDiff = nextPoint.x-previousPoint.x;
        float yDiff = nextPoint.y-previousPoint.y;

        float angle = (float) Math.toDegrees(Math.atan2(yDiff,xDiff));
        Log.d(TAG, "getAngle: "+angle);
        return angle;

    }

}
