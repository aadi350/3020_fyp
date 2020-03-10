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
    private static final int TRIM_THRESH_SIZE = 100, TRIM_REDUCE_SIZE = 50;
    private final int POINT_LOWER_THRESH = 25;
    private final String TAG = "SizeCheckHandler";
    private Vector3 objectSize = new Vector3();
    private Point3F[] boundingBox;
    private Vector3 storedObjectPosition = new Vector3(Vector3.zero());
    private ArrayList<Point3F> pointList = new ArrayList<>();
    QuickSort q = new QuickSort();
    private float highZ;
    Vector3 actualSize = new Vector3(Vector3.zero());
    private FitCodes NULL = FitCodes.NONE;

    private int currentPointSize = 0, prevPointSize = 0;


    public FitCodes checkIfFits(ObjectCodes objectCode, Vector3 nodePosition, FloatBuffer pointBuffer, Pose planePose) {
        if (pointBuffer == null || nodePosition == null || planePose == null)  return NULL;
        Log.d(TAG, "checkIfFits()");

        if (!pointBuffer.hasRemaining()) return NULL;
        if (pointBuffer.remaining() < POINT_LOWER_THRESH) return NULL;

        getValidPoints(pointBuffer, nodePosition, planePose);

        pointList.trimToSize();
        pointList = trimList(pointList);
        if (pointList.size() < POINT_LOWER_THRESH) return NULL;

        if (enoughPoints(pointList)) try {
            boundingBox = TwoDimensionalOrientedBoundingBox.getOBB(pointList);
            highZ = q.getHighestZ(this.pointList) - planePose.ty();

            actualSize .set(getBoxLength(boundingBox), getBoxWidth(boundingBox), highZ);

            switch (objectCode) {
                case CARRYON:
                    objectSize = CarryOnBuilder.getObjectSize();
                case DUFFEL:
                    objectSize = DuffelBuilder.getObjectSize();
                case PERSONAL:
                    objectSize = PersonalItemBuilder.getObjectSize();
            }
        } catch (Exception i) {
            Log.w(TAG, i.getLocalizedMessage());
        }

        if (actualSize.equals(Vector3.zero())) return FitCodes.NONE;


        boolean fits = compareLimits(objectSize, actualSize);
        return (fits) ? FitCodes.FIT : FitCodes.LARGE;
    }

    private ArrayList<Point3F> trimList(ArrayList<Point3F> pointList) {
        if (pointList.size() > TRIM_THRESH_SIZE) {
            int lim = pointList.size();
            ArrayList<Point3F> temp = new ArrayList<>();
            for (int i = lim - 25; i < lim; i++) {
                temp.add(pointList.get(i));
            }
        }
        return pointList;
    }

    private boolean enoughPoints(ArrayList<Point3F> pointList) {
        final int POINT_INCREASE_THRESH = 15;
        prevPointSize = currentPointSize;
        currentPointSize = pointList.size();
        return (currentPointSize > prevPointSize + POINT_INCREASE_THRESH);
    }

    private void getValidPoints(FloatBuffer pointBuffer, Vector3 nodePosition, Pose planePose) {

        if (pointBuffer == null) {
            Log.w(TAG, "PointBuffer NULL");
            return;
        }
        if (nodePosition == null)  {
            Log.w(TAG, "NodePosition null");
            return;
        }
        Log.d(TAG,"Buffer: " + pointBuffer.remaining());
        pointList.addAll(PointFilter.getValidPoints(pointBuffer,nodePosition,planePose));
        Log.d(TAG,"PointList Size: " + pointList.size());
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

    public boolean emptyPointListOnMove(Vector3 currentPosition) {
        float THRESH = 0.001f;
        if (currentPosition == null) return false;
        boolean xDif = Math.abs(currentPosition.x - storedObjectPosition.x) > THRESH;
        boolean yDif =Math.abs(currentPosition.y - storedObjectPosition.y) > THRESH;
        boolean zDif = Math.abs(currentPosition.z - storedObjectPosition.z) > THRESH;
        if (xDif || yDif || zDif) {
            pointList.clear();
            storedObjectPosition.set(currentPosition);
            Log.d(TAG,"pointList cleared");
            Log.d(TAG,currentPosition.toString());
            return true;
        }
        Log.d(TAG,"emptyListOnMove(): " + currentPosition.toString());
        return false;
    }

    public float[] getBoxDimLW(Point3F[] boundingBox) {
        float[] inputNullArray = {-1f,-1f};
        if (boundingBox == null || boundingBox.length != 4) return inputNullArray;

        float xOne = boundingBox[0].x;
        float xTwo = boundingBox[3].x;
        float yOne = boundingBox[0].z;
        float yTwo = boundingBox[3].z;

        float sideOne = (float) Math.sqrt(Math.pow(xTwo-xOne,2) + Math.pow(yTwo-yOne,2));

        float xThree = boundingBox[1].x;
        float yThree = boundingBox[1].z;

        float sideTwo = (float) Math.sqrt(Math.pow(xThree-xOne,2) + Math.pow(yThree-yOne,2));

        if (sideOne > sideTwo) {
            float[] boxDim = {sideOne,sideTwo};
            return boxDim;
        }

        float[] boxDim = {sideTwo,sideOne};
        return boxDim;

    }


    private boolean compareLimits(Vector3 ref, Vector3 actual) {
        return (
                        (ref.x >= actual.x) &&
                        (ref.y >= actual.y) &&
                        (ref.z >= actual.z)
                );
    }



}
