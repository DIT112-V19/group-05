package com.example.hajken;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

import static com.example.hajken.Bluetooth.TAG;

public class CanvasView extends View {

    public int width;
    public int height;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Path mPath;
    private Paint mPaint;
    private float mX,mY;
    private static final int ZERO = 0;
    private static final float TOLERANCE = 5; /// ???????
    ArrayList<Float> listOfXCoordinates = new ArrayList<>();
    ArrayList<Float> listOfYCoordinates = new ArrayList<>();
    Context context;


    public CanvasView(Context context, AttributeSet attributeSet){
        super(context,attributeSet);
        this.context = context;

        mPath = new Path();
        mPaint = new Paint();

        mPaint.setAntiAlias(true); // ????
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeWidth(10f);

        setDrawingCacheEnabled(true);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH){
        super.onSizeChanged(w,h,oldW,oldH);

        this.width = w;
        this.height = h;

        mBitmap = Bitmap.createBitmap(w,h,Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
    }

    private void startTouch(float x, float y){
        mPath.moveTo(x,y);
        mX = x;
        mY = y;
        Log.d(TAG, "startTouch: "+"X:"+x+"y"+y);
    }

    private void moveTouch(float x, float y){
        float dx = Math.abs(x-mX);
        float dy = Math.abs(y-mY);

        Log.d(TAG, "moveTouch: "+"x:"+x+"y:"+y);

        if (dx >= TOLERANCE || dy >= TOLERANCE) { // kolla upp detta
            mPath.quadTo(mX,mY,(x + mX) / 2,(y + mY) / 2); // kolla upp detta
            mX = x;
            mY = y;
        }
    }

    private void upTouch(){
        mPath.lineTo(mX,mY);
        Log.d(TAG, "upTouch: "+"X:"+mX+"Y:"+mY);
    }

    public void clearCanvas(){
        mPath.reset();
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN :

                //Reset/clear canvas and lists of coordinates
                clearCanvas();
                listOfXCoordinates.clear();
                listOfYCoordinates.clear();

                startTouch(x,y);
                listOfXCoordinates.add(x);
                listOfYCoordinates.add(y);
                invalidate();
                break;
        }

        switch (event.getAction()){
            case MotionEvent.ACTION_MOVE :
                //if (x > width || x < ZERO || y > height || y < ZERO){
                  //  event.setAction(MotionEvent.ACTION_UP); // not the most beautiful solution :)
                    //break;
                //}
                moveTouch(x,y);
                listOfXCoordinates.add(x);
                listOfYCoordinates.add(y);
                invalidate();
                break;
        }

        switch (event.getAction()){
            case MotionEvent.ACTION_UP :
                upTouch();
                listOfXCoordinates.add(x);
                listOfYCoordinates.add(y);
                invalidate();
                break;
        }
        return true;
    }

    public void coordinateHandling(ArrayList<Float> x, ArrayList<Float> y){
    }

    @Override
    public void onDraw(Canvas canvas){
         super.onDraw(canvas);

         canvas.drawBitmap(mBitmap, 0,0, mPaint);
         canvas.drawPath(mPath, mPaint);

    }
}
