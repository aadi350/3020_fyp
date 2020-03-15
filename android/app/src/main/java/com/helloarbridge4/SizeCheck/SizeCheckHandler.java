package com.helloarbridge4.SizeCheck;

import android.util.Log;

import com.google.ar.core.Pose;
import com.google.ar.sceneform.math.Vector3;
import com.helloarbridge4.Builder.CarryOnBuilder;
import com.helloarbridge4.Builder.DuffelBuilder;
import com.helloarbridge4.Builder.PersonalItemBuilder;
import com.helloarbridge4.FitCodes;
import com.helloarbridge4.Object.ObjectCodes;
import com.helloarbridge4.Point3F.Point3F;
import com.helloarbridge4.Point3F.PointFilter;
import com.helloarbridge4.SizeCheck.MinBoundingBox.TwoDimensionalOrientedBoundingBox;

import java.nio.FloatBuffer;
import java.util.ArrayList;

public class SizeCheckHandler {
    private static final int TRIM_THRESH_SIZE = 450, TRIM_REDUCE_SIZE = 50;
    private final String TAG = "SizeCheckHandler";
    private Vector3 objectSize = new Vector3();
    private Point3F[] boundingBox;
    private Vector3 storedObjectPosition = new Vector3(Vector3.zero());
    private ArrayList<Point3F> pointList = new ArrayList<>();
    private QuickSort q = new QuickSort();
    private float highZ;
    private Vector3 actualSize = new Vector3(Vector3.zero());
    private FitCodes NULL = FitCodes.NONE;

    private int currentPointSize = 0, prevPointSize = 0;
    private int frameCount = 0;
    private final int FRAME_THRESH  = 60;

    public FitCodes checkIfFits(ObjectCodes objectCode, Vector3 nodePosition, FloatBuffer pointBuffer, Pose planePose) {
        if (pointBuffer == null || nodePosition == null || planePose == null)  return NULL;
        Log.d(TAG, "checkIfFits() start");

        if (!pointBuffer.hasRemaining()) return NULL;
        int POINT_LOWER_THRESH = 25;
        if (pointBuffer.remaining() < POINT_LOWER_THRESH) return NULL;

        getValidPoints(pointBuffer, nodePosition, planePose);
        frameCount++;
        //pointList = trimList(pointList);

        if (pointList.size() < POINT_LOWER_THRESH) {
            return NULL;
        }

        if (frameCount % FRAME_THRESH != 0) return null;

        try {
            ArrayList<Point3F> points = this.pointList;
            boundingBox = TwoDimensionalOrientedBoundingBox.getOBB(points);

            highZ = q.getHighestZ(this.pointList) - planePose.ty();

            float actualLength = getBoxLength();
            float actualWidth = getBoxWidth();

            if (actualLength != 0f && actualWidth != 0f) {
                actualSize.set(actualLength, actualWidth, highZ);
                Log.d(TAG,"CALC");
                switch (objectCode) {
                    case CARRYON:
                        objectSize = CarryOnBuilder.getObjectSize();
                    case DUFFEL:
                        objectSize = DuffelBuilder.getObjectSize();
                    case PERSONAL:
                        objectSize = PersonalItemBuilder.getObjectSize();
                }
            }
        } catch (Exception i) {
            Log.w(TAG, i.getLocalizedMessage());
        }

        if (actualSize.equals(Vector3.zero())) return FitCodes.NONE;


        boolean fits = compareLimits(objectSize, actualSize);
        return (fits) ? FitCodes.FIT : FitCodes.LARGE;
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

    private ArrayList<Point3F> getValidPoints(FloatBuffer pointBuffer, Vector3 nodePosition, Pose planePose) {

        if (pointBuffer == null) {
            Log.w(TAG, "PointBuffer NULL");
            return this.pointList;
        }
        if (nodePosition == null)  {
            Log.w(TAG, "NodePosition null");
            return null;
        }
        Log.d(TAG,"Buffer: " + pointBuffer.remaining());
        pointList.addAll(PointFilter.getValidPoints(pointBuffer,nodePosition,planePose));
        return pointList;
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

    public float getHighZ() {
        if (this.highZ < 0f) return -1f;
        return this.highZ;
    }


    public float[] getBoxDimLW(Point3F[] boundingBox) {
        float[] inputNullArray = {-1f,-1f};
        if (boundingBox == null || boundingBox.length != 4) return inputNullArray;


        float xOne = boundingBox[0].x;
        float xTwo = boundingBox[3].x;
        float zOne = boundingBox[0].z;
        float zTwo = boundingBox[3].z;

        float sideOne = (float) Math.sqrt((xTwo-xOne)*(xTwo-xOne) + (zTwo-zOne)*(zTwo-zOne));

        float xThree = boundingBox[1].x;
        float zThree = boundingBox[1].z;

        float sideTwo = (float) Math.sqrt((xThree-xOne)*(xThree-xOne) + (zThree-zOne)*(zThree-zOne));

        if (sideOne == Float.NaN || sideTwo == Float.NaN) {
            this.pointList.clear();
            return new float[] {-1f,-1f};
        }

        if (sideOne > sideTwo) {
            float[] boxDim = {sideOne,sideTwo};
            return boxDim;
        }

        float[] boxDim = {sideTwo,sideOne};
        return boxDim;

    }


    private boolean compareLimits(Vector3 ref, Vector3 actual) {
        final float DIM_BUFFER = 0.05f;
        return (
//                        (ref.x + DIM_BUFFER >= actual.x) &&
                        (ref.y + DIM_BUFFER >= actual.y) //&&
//                        (ref.z + DIM_BUFFER >= actual.z)
                );
    }



}
