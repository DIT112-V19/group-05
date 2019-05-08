package com.example.hajken;

import android.content.Context;
import android.graphics.PointF;
import android.util.Log;

import java.util.ArrayList;

import static android.support.constraint.Constraints.TAG;

public class CoordinateConverter {

    private String instructions;
    private static CoordinateConverter mInstance = null;
    private Context myContext;
    private int speed;

    private CoordinateConverter(Context context){
        myContext = context;
    }

    public static CoordinateConverter getInstance(Context context){
        if (mInstance == null){
            mInstance = new CoordinateConverter(context);
        }
        return mInstance;
    }

    public String returnString(ArrayList<PointF> validPoints){

        instructions = "<";
        Log.d(TAG, "returnString: ");

        instructions = instructions.concat("l,"+(((validPoints.size()-1)*4)+4));

        //Change speed of vehicle
        instructions = instructions.concat(",v,"+getSpeed()+",");

        instructions = instructions.concat("r,0,"); // amount of loops - needs to be adaptable later

        ArrayList<Float> angles = new ArrayList<>();
        float prevAngle = 0;

        for(int i = 0; i < validPoints.size()-1;i++){

            if (MathUtility.getInstance(myContext).getMagnitude(validPoints.get(i),validPoints.get(i+1)) > 20){
                angles = MathUtility.getInstance(myContext).getRotation(validPoints.get(i),validPoints.get(i+1),prevAngle);
                prevAngle = angles.get(1); // previous degrees
                instructions = instructions.concat("t,"+angles.get(0)); // actual rotation
                instructions = instructions.concat(",");
                instructions = instructions.concat("f,"+MathUtility.getInstance(myContext).getMagnitude(validPoints.get(i),validPoints.get(i+1)));

                Log.d(TAG, "returnString: "+validPoints.size());
                Log.d(TAG, "returnString: "+i);
                if (i+1 == validPoints.size()-1) {
                    instructions = instructions.concat(">!");
                } else {
                    instructions = instructions.concat(",");
                }
            }
        }
        Log.d(TAG, "returnString: "+instructions);
        return instructions;
    }

    public void setSpeed(int speedChoice){
        this.speed = speedChoice;
    }

    public int getSpeed(){
        return this.speed;
    }
}
