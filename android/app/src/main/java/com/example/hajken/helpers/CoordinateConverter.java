package com.example.hajken.helpers;

import android.content.Context;
import android.graphics.PointF;
import android.util.Log;
import java.util.ArrayList;
import static android.support.constraint.Constraints.TAG;

public class CoordinateConverter {

    private String instructions;
    private static CoordinateConverter mInstance = null;
    private Context mContext;
    private int speed;
    private int nrOfLoops;
    private final int PERM_EXTRA_ARR_SIZE_ARDUINO = 4;
    private MathUtility mMathUtility;

    private CoordinateConverter(Context context){
        mContext = context;
        mMathUtility = MathUtility.getInstance(mContext);

    }


    public static CoordinateConverter getInstance(Context context){
        if (mInstance == null){
            mInstance = new CoordinateConverter(context);
        }
        return mInstance;
    }


    public String returnInstructions(ArrayList<PointF> validPoints){

        instructions = "<";
        Log.d(TAG, "returnInstructions: ");

        //Set length of string (for encoding purposes in Arduino)
        instructions = instructions.concat("l,"+(((validPoints.size()-1)*PERM_EXTRA_ARR_SIZE_ARDUINO)+PERM_EXTRA_ARR_SIZE_ARDUINO));

        //Set speed of vehicle for route
        instructions = instructions.concat(",v,"+getSpeed()+",");

        //Set amount of loops of vehicle
        instructions = instructions.concat("r,"+getNrOfLoops()+","); // amount of loops - needs to be adaptable later

        ArrayList<Float> angles;
        float prevAngle = 0;

        for(int i = 0; i < validPoints.size()-1;i++){

            if (mMathUtility.getMagnitude(validPoints.get(i),validPoints.get(i+1)) > 20){
                angles = mMathUtility.getRotation(validPoints.get(i),validPoints.get(i+1),prevAngle);
                prevAngle = angles.get(1); // previous degrees
                instructions = instructions.concat("t,"+angles.get(0)); // actual rotation
                instructions = instructions.concat(",");
                instructions = instructions.concat("f,"+mMathUtility.getMagnitude(validPoints.get(i),validPoints.get(i+1)));

                Log.d(TAG, "returnInstructions: "+validPoints.size());
                Log.d(TAG, "returnInstructions: "+i);
                if (i+1 == validPoints.size()-1) {
                    instructions = instructions.concat(">!");
                } else {
                    instructions = instructions.concat(",");
                }
            }
        }
        Log.d(TAG, "returnInstructions: "+instructions);
        return instructions;
    }

    public void setSpeed(int speedChoice){
        this.speed = speedChoice;
    }

    public int getSpeed(){
        return this.speed;
    }

    public int getNrOfLoops() {
        return nrOfLoops;
    }

    public void setNrOfLoops(int nrOfLoops) {
        this.nrOfLoops = nrOfLoops;
    }
}
