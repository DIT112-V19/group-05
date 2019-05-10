package com.example.hajken.helpers;

import android.content.Context;
import android.graphics.PointF;
import android.util.AndroidRuntimeException;
import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class MathUtility {

    private static final String TAG = "MathUtility";
    private static MathUtility mInstance = null;
    private Context myContext;

    private MathUtility(Context context){
        myContext = context;
    }

    public static MathUtility getInstance(Context context){
        if (mInstance == null){
            mInstance = new MathUtility(context);
        }
        return mInstance;
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


    //https://en.wikipedia.org/wiki/Ramer%E2%80%93Douglas%E2%80%93Peucker_algorithm

    public ArrayList<PointF> rdpSimplifier(ArrayList<PointF> listOfCoordinates, double epsilon){

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
            ArrayList<PointF> recResults1 = rdpSimplifier( subList1, epsilon);
            ArrayList<PointF> recResults2 = rdpSimplifier( subList2, epsilon);
            resultList.addAll(recResults1);
            resultList.addAll(recResults2);

        } else {
            Log.d(TAG, "rdpSimplifier: ELSE " + listOfCoordinates.toString());
            resultList.add(listOfCoordinates.get(0));
            resultList.add(listOfCoordinates.get(end));
        }
        return resultList;
    }

    public float getMagnitude(PointF pointA, PointF pointB){
        return (float) Math.sqrt(Math.pow((pointB.x-pointA.x),2)+Math.pow((pointB.y-pointA.y),2));
    }

    public ArrayList<Float> getRotation(PointF pointA, PointF pointB, float prevDegrees){
        float diffY = pointB.y - pointA.y;
        float diffX = pointB.x - pointA.x;
        float atan = (float) Math.atan(Math.abs(diffY) / Math.abs(diffX));

        float degrees = (float) Math.toDegrees(atan);
        float actualRotation;

        ArrayList<Float> angles = new ArrayList<>();

        if (diffY > 0){

            //in quadrant 1 --- rotate right
            if (diffX > 0){
                degrees = 90 - degrees;

            } else {
                // in quadrant 2 --- rotate left
                degrees = (90-degrees)*-1;

            }
        } else {

            //in quadrant 3 ----- rotate left
            if (diffX < 0){
                degrees = (degrees+90)*-1;


            } else {
                //in quadrant 4 --- rotate right
               degrees = degrees+90;

            }
        }

        actualRotation = degrees-prevDegrees;

        if (actualRotation > 180){
            actualRotation = actualRotation-180;
        }

        if (actualRotation < -180){
            actualRotation = 360 + actualRotation;
        }

        angles.add(actualRotation);
        angles.add(degrees);
        Log.d(TAG, "degree : here ");

        return angles;
    }

}
