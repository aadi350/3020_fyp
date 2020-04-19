package com.reactlibrary.SizeCheck;

import android.util.Log;

import com.google.ar.core.Pose;
import com.google.ar.core.TrackingState;
import com.google.ar.sceneform.math.Vector3;
import com.reactlibrary.Builder.CarryOnBuilder;
import com.reactlibrary.Builder.DuffelBuilder;
import com.reactlibrary.Builder.PersonalItemBuilder;
import com.reactlibrary.FitCodes;
import com.reactlibrary.Object.ObjectCodes;
import com.reactlibrary.Point3F.Point3F;
import com.reactlibrary.Point3F.PointFilter;
import com.reactlibrary.SizeCheck.MinBoundingBox.TwoDimensionalOrientedBoundingBox;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Collections;

import static java.lang.Float.NaN;

public class SizeCheckHandler {
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
    private final int CALC_LIMIT = 4;

    private int POINT_LOWER_THRESH = 15;
    private int POINT_LIST_THRESH = 75;

    private ArrayList<Point3F> points = new ArrayList<>();

    public void flushListWhenNotTrackging(TrackingState trackingState) {
        if (trackingState == TrackingState.STOPPED) {
            pointList.clear();
        }

    }

    public FitCodes checkIfFits(ObjectCodes objectCode, Vector3 nodePosition, FloatBuffer pointBuffer, Vector3 anchorNodePosition) {
        if (pointBuffer == null || nodePosition == null || anchorNodePosition == null)  return NULL;
        Log.d(TAG, "checkIfFits() start " + anchorNodePosition.toString());

        if (!pointBuffer.hasRemaining()) return NULL;


        //if (pointBuffer.remaining() < POINT_LOWER_THRESH) return NULL;
        Log.d(TAG,"try getValid Points");
        try {
            pointList.addAll(getValidPoints(pointBuffer, nodePosition, anchorNodePosition));
        } catch (NullPointerException e) {
            Log.d(TAG, e.getLocalizedMessage());
        }


        Log.d(TAG, "try OBB");
        try {

            if (pointList.size() < POINT_LIST_THRESH) {
                Log.d(TAG, "not Enough Points");
                return NULL;
            }
            if (calcCount > CALC_LIMIT) return NULL;
            calcCount++;

            ArrayList<Point3F> points = pointList;
            Log.d(TAG,"getOBB() run");
            boundingBox = TwoDimensionalOrientedBoundingBox.getOBB(points);
            points.addAll(pointList);


            highPointVal = Math.abs(q.getHighestPoint(points) + anchorNodePosition.y);

            float actualLength = getBoxLength();
            float actualWidth = getBoxWidth();

            Log.d("DIM", actualLength + " " + actualWidth + " " + highPointVal);

            if (actualLength != 0f && actualWidth != 0f) {
                actualSize.set(actualLength, actualWidth, highPointVal);
                if (actualSize.equals(Vector3.zero())) return FitCodes.NONE;
                switch (objectCode) {
                    case CARRYON:
                        objectSize.set(CarryOnBuilder.getObjectSize());
                    case DUFFEL:
                        objectSize.set(DuffelBuilder.getObjectSize());
                    case PERSONAL:
                        objectSize.set(PersonalItemBuilder.getObjectSize());
                }
            }
            return  compareLimits(objectSize, actualSize);
        } catch (Exception i) {
            Log.w(TAG, i.getLocalizedMessage());
        }



        Log.d(TAG, "object: " + objectSize.toString());
        Log.d(TAG, "actual" + actualSize.toString());

        return FitCodes.NONE;
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
        return PointFilter.getValidPoints(pointBuffer,nodePosition,anchorNodePosition);
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
                (       (ref.x + DIM_BUFFER >= actual.x) &&
                        (ref.y + DIM_BUFFER >= actual.y) &&
                        (ref.z + DIM_BUFFER >= actual.z)) ?
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


}