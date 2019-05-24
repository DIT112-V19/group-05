package com.example.hajken.helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import com.example.hajken.R;
import java.util.ArrayList;

public class CanvasView extends View {

    public int width;
    public int height;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Path mPath;
    private Paint mPaint;
    private Path mStartPoint;
    private Paint mStartPointPaint;
    private Path mActualPath;
    private Paint mActualPathPaint;
    private static final String TAG = "CanvasView";
    private float mX, mY;
    private ArrayList<PointF> validPoints;
    private ArrayList<PointF> actualPathPoints;
    private static final int ZERO = 0;
    private static final float TOLERANCE = 5; /// ???????

    public ArrayList<PointF> getListOfCoordinates() {
        return listOfCoordinates;
    }

    ArrayList<PointF> listOfCoordinates = new ArrayList<>();
    Context context;

    public CanvasView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.context = context;

        //Settings for the drawn path
        mPath = new Path();
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(getResources().getColor(R.color.actual_path_color));
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeWidth(10f);

        //Settings for the starting point
        mStartPoint = new Path();
        mStartPointPaint = new Paint();
        mStartPointPaint.setAntiAlias(true);
        mStartPointPaint.setColor(getResources().getColor(R.color.start_point_color));
        mStartPointPaint.setStyle(Paint.Style.STROKE);
        mStartPointPaint.setStrokeJoin(Paint.Join.ROUND);
        mStartPointPaint.setStrokeWidth(10f);

        //Settings for the actual path
        mActualPath = new Path();
        mActualPathPaint = new Paint();
        mActualPathPaint.setAntiAlias(true);
        mActualPathPaint.setColor(getResources().getColor(R.color.actual_path_color));
        mActualPathPaint.setStyle(Paint.Style.STROKE);
        mActualPathPaint.setStrokeJoin(Paint.Join.ROUND);
        mActualPathPaint.setStrokeWidth(10f);

        setDrawingCacheEnabled(true);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);

        this.width = w;
        this.height = h;

        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
    }

    private void startTouch(float x, float y) {
        mPath.moveTo(x, y);
        mStartPoint.addCircle(x,y,30,Path.Direction.CW);
        mX = x;
        mY = y;
    }

    private void moveTouch(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);

        if (dx >= TOLERANCE || dy >= TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;
        }
    }

    private void upTouch() {
        mPath.lineTo(mX, mY);
        //Changes the color of the current drawn path after upTouch
        mPaint.setColor(getResources().getColor(R.color.drawn_path_color));
    }

    public void clearCanvas(){
        mPath.reset();
        mStartPoint.reset();
        mActualPath.reset();
        invalidate();

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float x = event.getX();
        float y = event.getY();

        Log.d(TAG, "onTouchEvent: "+x+" y "+y);


        //Inverts y-value so that it is the correct rotation for user
        float invertedY = mBitmap.getHeight() - y;

        Log.d(TAG, "onTouchEvent: Inverted "+x+" y "+invertedY);


        //this makes sure that values are not stored when touching outside of canvas
        if (x > width || x < ZERO || y > height || y < ZERO) {
            event.setAction(MotionEvent.ACTION_CANCEL);
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.d(TAG, "onTouchEvent: STEP A");

                //Clear canvas and coordinates
                clearCanvas();
                listOfCoordinates.clear();
                if (validPoints != null){
                    validPoints.clear();
                }
                startTouch(x, y);
                PointF downPoint = new PointF();
                downPoint.set(x, invertedY);
                listOfCoordinates.add(downPoint);
                invalidate();
                break;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                moveTouch(x, y);
                PointF movePoint = new PointF();
                movePoint.set(x, invertedY);
                listOfCoordinates.add(movePoint);
                invalidate();
                break;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                upTouch();
                PointF upPoint = new PointF();
                upPoint.set(x, invertedY);
                listOfCoordinates.add(upPoint);
                validPoints = MathUtility.getInstance(getContext()).rdpSimplifier(getListOfCoordinates(), 65.0);
                actualPathPoints = new ArrayList<>();
                copyList(validPoints);
                displayActualPath(actualPathPoints);


                invalidate();
                break;
        }
        return true;
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawBitmap(mBitmap, 0, 0, mPaint);
        canvas.drawPath(mPath, mPaint);
        canvas.drawPath(mStartPoint,mStartPointPaint);
        canvas.drawPath(mActualPath,mActualPathPaint);

    }

    public Bitmap getBitmap() {
         this.setDrawingCacheEnabled(true);
         this.buildDrawingCache();
         Bitmap bmp = Bitmap.createBitmap(this.getDrawingCache());
         this.setDrawingCacheEnabled(true);

         return bmp;
    }

    public void copyList(ArrayList<PointF> validPoints){

        for (int i = 0; i < validPoints.size(); i++){
            PointF point = new PointF();
            point.x = validPoints.get(i).x;
            point.y = validPoints.get(i).y;
            this.actualPathPoints.add(point);
        }

    }


    public void displayActualPath(ArrayList<PointF> actualPathPoints){
        //Inverts all y-values so that it is the correct rotation for bitMap :)
        for (int i = 0; i < actualPathPoints.size();i++){
            actualPathPoints.get(i).set(actualPathPoints.get(i).x,mBitmap.getHeight()-actualPathPoints.get(i).y);
        }

        //Draw path
        mActualPath.moveTo(actualPathPoints.get(0).x,actualPathPoints.get(0).y);
        float bX = actualPathPoints.get(0).x;
        float bY = actualPathPoints.get(0).y;

        float dx;
        float dy;

        for (int i = 1; i < actualPathPoints.size(); i++){ // draw the rest of the points
            dx = Math.abs(bX - mX);
            dy = Math.abs(bY - mY);
            if (dx >= TOLERANCE || dy >= TOLERANCE) {
                mActualPath.quadTo(bX, bY, (actualPathPoints.get(i).x + bX) / 2, (actualPathPoints.get(i).y + bY) / 2);
                bX = actualPathPoints.get(i).x;
                bY = actualPathPoints.get(i).y;
            }
        }

        mActualPath.lineTo(actualPathPoints.get(actualPathPoints.size()-1).x,actualPathPoints.get(actualPathPoints.size()-1).y);
    }

    public ArrayList<PointF> getValidPoints() {
        return validPoints;
    }


}
