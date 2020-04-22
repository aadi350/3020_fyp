package com.reactlibrary.SizeCheck;

import android.graphics.PointF;
import android.util.Log;


import com.google.ar.core.TrackingState;
import com.google.ar.sceneform.math.Vector3;
import com.reactlibrary.Builder.CarryOnBuilder;
import com.reactlibrary.Builder.DuffelBuilder;
import com.reactlibrary.Builder.PersonalItemBuilder;
import com.reactlibrary.FitCodes;
import com.reactlibrary.Object.ObjectCodes;
import com.reactlibrary.Point3F.Point3F;
import com.reactlibrary.Point3F.PointFilter;
import com.reactlibrary.SizeCheck.MinBoundingBox.Rectangle;
import com.reactlibrary.SizeCheck.MinBoundingBox.TwoDimensionalOrientedBoundingBox;

import java.nio.FloatBuffer;
import java.util.ArrayList;


public class SizeCheckHandler {
    private static final int DELAY_THRESH = 200;
    private int delayCount = 0;

    private final String TAG = "SizeCheckHandler";
    private Vector3 objectSize = new Vector3();
    private Point3F[] boundingBox;
    private ArrayList<Point3F> pointList = new ArrayList<>();
    private QuickSort q = new QuickSort();
    private float highPointVal;
    private Vector3 actualSize = new Vector3(Vector3.zero());
    private FitCodes NULL = FitCodes.NONE;

    private int currentPointSize = 0, prevPointSize = 0;
    private int calcCount = 0;
    private final int CALC_LIMIT = 10;

    private int POINT_LOWER_THRESH = 15;
    private int POINT_LIST_THRESH = 200;

    private ArrayList<Point3F> points = new ArrayList<>();

    public void flushListWhenNotTrackging(TrackingState trackingState) {
        if (trackingState == TrackingState.STOPPED) {
            pointList.clear();
        }

    }

    public FitCodes checkIfFits(ObjectCodes objectCode, Vector3 nodePosition, FloatBuffer pointBuffer, Vector3 anchorNodePosition) {

        if (!readyToMeasure()) return NULL;
        incrementCalcCount();

        if (pointBuffer == null || nodePosition == null || anchorNodePosition == null)  return NULL;
        if (!pointBuffer.hasRemaining()) return NULL;

        Log.d(TAG,"try getValid Points");
        try {
            pointList.addAll(getValidPoints(pointBuffer, nodePosition, anchorNodePosition));

            if (pointList.size() < POINT_LIST_THRESH) {
                Log.d(TAG, "Not enough points:" + pointList.size());
                return NULL;
            }

            if (calcCount % CALC_LIMIT != 0 || calcCount > CALC_LIMIT*12) {
                Log.d(TAG,"CALC_LIMIT");
                return NULL;
            }

            Log.d(TAG, "calcCount approved");

            Log.d(TAG, "OBB");
            ArrayList<Point3F> points = pointList;
            Rectangle boundingBox = TwoDimensionalOrientedBoundingBox.getOBB(points);
            float actualLength = (float) boundingBox.height;
            float actualWidth = (float) boundingBox.width;
            points = pointList;
            //points.addAll(pointList);

            highPointVal = Math.abs(q.getHighestPoint(points) + anchorNodePosition.y);






            if (actualLength != 0f && actualWidth != 0f) {
                actualSize.set(actualLength, actualWidth, highPointVal);
                Log.d(TAG,String.valueOf(objectCode));
                switch (objectCode) {
                    case CARRYON:
                        objectSize.set(CarryOnBuilder.getObjectSize());
                    case DUFFEL:
                        objectSize.set(DuffelBuilder.getObjectSize());
                    case PERSONAL:
                        objectSize.set(PersonalItemBuilder.getObjectSize());
                }
                Log.d(TAG, "actual" + actualSize.toString());
                Log.d(TAG, "object: " + objectSize.toString());
            } else {
                return FitCodes.NONE;
            }
            return compareLimits(objectSize, actualSize);
        } catch (NullPointerException e) {
            Log.d(TAG, e.getLocalizedMessage());
        }

        return FitCodes.NONE;
    }

    private boolean readyToMeasure() {
        delayCount++;
        return delayCount >= DELAY_THRESH;
    }

    private boolean enoughPoints(ArrayList<Point3F> pointList) {
        final int POINT_INCREASE_THRESH = 4;
        currentPointSize = pointList.size();
        if ((currentPointSize - prevPointSize) > POINT_INCREASE_THRESH) {
            prevPointSize = currentPointSize;
            return true;
        }
        return false;
    }

    private ArrayList<Point3F> getValidPoints(FloatBuffer pointBuffer, Vector3 nodePosition, Vector3 anchorNodePosition) throws NullPointerException{

        if (pointBuffer == null) {
            throw new NullPointerException("pointBuffer null");
        }
        if (nodePosition == null)  {
            throw new NullPointerException("Node Position Null");
        }
        Log.d(TAG,"Buffer: " + pointBuffer.remaining());
        ArrayList<Point3F> unFilteredList = PointFilter.convertBufferToList(pointBuffer);
        ArrayList<Point3F> confPoints = PointFilter.filterByConfidence(unFilteredList);
        ArrayList<Point3F> closePoints = PointFilter.filterByRegion(confPoints,anchorNodePosition);
        ArrayList<Point3F> groundRemoved = PointFilter.filterGround(closePoints, anchorNodePosition);
        Log.d(TAG, confPoints.size() + " " + closePoints.size() + " " + groundRemoved.size());
        return groundRemoved;
    }




    public float[] getBoxDimLW(Point3F[] boundingBox) {
        float[] inputNullArray = {-1f,-1f};
        if (boundingBox == null || boundingBox.length != 4) return inputNullArray;


        float xOne = boundingBox[1].x;
        float xTwo = boundingBox[2].x;
        float zOne = boundingBox[1].z;
        float zTwo = boundingBox[2].z;

        float sideOne = (float) Math.sqrt(Math.pow(xTwo-xOne,2) + Math.pow(zTwo-zOne,2));

        float xThree = boundingBox[3].x;
        float zThree = boundingBox[3].z;

        float sideTwo = (float) Math.sqrt((xThree-xTwo)*(xThree-xTwo) + (zThree-zTwo)*(zThree-zTwo));
        Log.d("DIM", xOne + " " + xTwo + " " + zOne + " " + zTwo);
        if (Float.isNaN(sideOne) || Float.isNaN(sideTwo)) {
            //this.pointList.clear();
            return new float[] {-1f,-1f};
        }

        if (sideOne > sideTwo) {
            float[] boxDim = {sideOne,sideTwo};
            return boxDim;
        }

        float[] boxDim = {sideTwo,sideOne};
        return boxDim;

    }


    private FitCodes compareLimits(Vector3 ref, Vector3 actual) {
        final float DIM_BUFFER = 0.03f;
        FitCodes fits =
                (       (ref.x >= actual.x) &&
                        (ref.y >= actual.y) &&
                        (ref.z >= actual.z)) ?
                        FitCodes.FIT : FitCodes.LARGE;
        Log.d(TAG,fits.toString());
        return fits;
    }


    public float getBoxLength() {
        return getBoxDimLW(this.boundingBox)[0];
    }

    public float getBoxWidth() {
        return getBoxDimLW(this.boundingBox)[1];
    }

    public float getBoxLength(Point3F[] boundingBox) {
        return getBoxDimLW(boundingBox)[0];
    }

    public float getBoxWidth(Point3F[] boundingBox) {
        return getBoxDimLW(boundingBox)[1];
    }

    public float getHighPointVal() {
        if (this.highPointVal < 0f) return -1f;
        return this.highPointVal;
    }

    private void incrementCalcCount() {
        calcCount++;
    }

}