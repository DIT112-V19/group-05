package com.example.hajken;

import android.graphics.Point;
import android.graphics.PointF;
import android.util.Log;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;

public class MathUtility {

    private static final float DIFFERENCE_MARGIN = 20;
    private static final float DIFFERENCE_MARGIN_X = 10;
    private static final float DIFFERENCE_MARGIN_Y = 10;

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

    private double perpendicularDistance (PointF point, PointF lineStart, PointF lineEnd){
        double dx = lineEnd.x - lineStart.x;
        double dy = lineEnd.y - lineStart.y;

        double mag = Math.hypot(dx,dy);

        if (mag > 0.0) {
            dx /= mag;
            dy /= mag;
        }

        double pvx = point.x - lineStart.x;
        double pvy = point.y - lineStart.y;

        double pvdot = dx * pvx + dy * pvy;

        double ax = pvx - pvdot * dx;
        double ay = pvy - pvdot * dy;

        return Math.hypot(ax, ay);

    }

    public ArrayList<PointF> findPoints2(ArrayList<PointF> listOfCoordinates, double epsilon){

        double dmax = 0.0;
        int index = 0;
        int end = listOfCoordinates.size() - 1;

        for (int i = 2; i < end ; i++) {

            double d = perpendicularDistance(listOfCoordinates.get(i) , listOfCoordinates.get(0), listOfCoordinates.get(end));
            if (d > dmax){
                index = i;
                dmax = d;
            }
        }

        ArrayList<PointF> resultList = new ArrayList<>();

        if (dmax > epsilon) {

            ArrayList<PointF> subList1 = new ArrayList<>(listOfCoordinates.subList(0, index));
            ArrayList<PointF> subList2 = new ArrayList<>(listOfCoordinates.subList(index+1, end));
            ArrayList<PointF> recResults1 = findPoints2( subList1, epsilon);
            ArrayList<PointF> recResults2 = findPoints2( subList2, epsilon);
            resultList.addAll(recResults1);
            resultList.addAll(recResults2);
        } else {
            Log.d(TAG, "findPoints2: ELSE " + listOfCoordinates.toString());
            resultList.add(listOfCoordinates.get(0));
            resultList.add(listOfCoordinates.get(end));
        }
        return resultList;
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

        if (Math.abs(a1) < DIFFERENCE_MARGIN_Y && Math.abs(b1) < DIFFERENCE_MARGIN_X) {
            return false;
        }

        Log.d(TAG, "isNewVector: grades second to third"+Math.toDegrees(Math.atan2(a1,b1)));
        Log.d(TAG, "isNewVector: grades first to second"+Math.toDegrees(Math.atan2(a2,b2)));


        Log.d(TAG, "isNewVector: grades:"+(Math.toDegrees(Math.atan2(a1,b1))-Math.toDegrees(Math.atan2(a2,b2))));

        return Math.abs((Math.toDegrees(Math.atan2(a1,b1)))-(Math.toDegrees(Math.atan2(a2,b2)))) > DIFFERENCE_MARGIN  ;
    }

    public float getMagnitude(PointF pointA, PointF pointB){
        return (float) Math.sqrt(Math.pow((pointB.x-pointA.x),2)+Math.pow((pointB.y-pointA.y),2));
    }

}
