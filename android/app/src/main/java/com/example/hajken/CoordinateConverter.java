package com.example.hajken;

import android.graphics.PointF;
import android.util.Log;

import java.util.ArrayList;

import static android.support.constraint.Constraints.TAG;

public class CoordinateConverter {

    private String instructions;
    private MathUtility mathUtility = new MathUtility();

    public String returnString(ArrayList<PointF> validPoints){

        instructions = "<";
        Log.d(TAG, "returnString: ");

        instructions = instructions.concat("l,"+(((validPoints.size()-1)*4)+4));

        //Change speed of vehicle
        instructions = instructions.concat(",v,3,"); // this needs to adaptable later

        instructions = instructions.concat("r,0,"); // amount of loops - needs to be adaptable later

        float prevAngle = 0;
        float rotation;

        for(int i = 0; i < validPoints.size()-1;i++){

            if (mathUtility.getMagnitude(validPoints.get(i),validPoints.get(i+1)) > 20){
                rotation = mathUtility.getRotation2(validPoints.get(i),validPoints.get(i+1),prevAngle);
                prevAngle = rotation;
                instructions = instructions.concat("t,"+rotation);
                instructions = instructions.concat(",");
                instructions = instructions.concat("f,"+mathUtility.getMagnitude(validPoints.get(i),validPoints.get(i+1)));

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

}
