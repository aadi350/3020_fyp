package com.helloarbridge4.SizeCheck;

import android.util.Log;

import com.google.ar.sceneform.math.Vector3;
import com.helloarbridge4.Builder.CarryOnBuilder;
import com.helloarbridge4.Builder.DuffelBuilder;
import com.helloarbridge4.Builder.PersonalItemBuilder;
import com.helloarbridge4.Object.ObjectCodes;
import com.helloarbridge4.Point3F.Point3F;
import com.helloarbridge4.Point3F.PointFilter;

import java.nio.FloatBuffer;
import java.util.ArrayList;

public class SizeCheckHandler {
    private final int POINT_LOWER_THRESH = 100;
    private final String TAG = "SizeCheckHandler";
    private final String RECT_TAG = "Rectangle";
    private Vector3 objectSize = new Vector3();
    private Vector3 nodePosition;
    private Point3F[] boundingBox;
    private Vector3 storedObjectPosition = new Vector3(Vector3.zero());
    private ArrayList<Point3F> pointList = new ArrayList<>();
    private float highZ;


    public FitCodes checkIfFits(ObjectCodes objectCode, Vector3 nodePosition, FloatBuffer pointBuffer) {
        if (pointBuffer == null) return null;
        Log.d(TAG, "checkIfFits()");
        QuickSort q = new QuickSort();

        Vector3 actualSize = new Vector3(Vector3.zero());

        //Resets object measurement to accomodate new object position
        boolean newPosition = emptyPointListOnMove(nodePosition);
        if (newPosition) return FitCodes.NONE;

        //Ensures there are points to check and filters Points in PointCloud by distance to
        //placed object and object confidence score

        if (!pointBuffer.hasRemaining()) return null;
        getValidPoints(pointBuffer, storedObjectPosition);

        if (pointList.size() < POINT_LOWER_THRESH) return null;

        try {
            boundingBox = TwoDimensionalOrientedBoundingBox.getMinimumBoundingRectangle(pointList);
            highZ = q.getHighestZ(this.pointList);// - nodePosition.z;

            if (boundingBox.length != 4)return null;

            actualSize = new Vector3(getBoxLength(), getBoxWidth(), highZ);
            switch (objectCode) {
                case CARRYON:
                    objectSize = CarryOnBuilder.getObjectSize();
                case DUFFEL:
                    objectSize = DuffelBuilder.getObjectSize();
                case PERSONAL:
                    objectSize = PersonalItemBuilder.getObjectSize();
            }

        } catch (Exception i ) {
            Log.w(TAG, i.getLocalizedMessage());
        }

        if (actualSize.equals(Vector3.zero())) return null;


        boolean fits = compareLimits(objectSize, actualSize);
        return (fits) ? FitCodes.FIT : FitCodes.LARGE;
    }


    private void getValidPoints(FloatBuffer pointBuffer, Vector3 nodePosition) {

        if (pointBuffer == null) {
            Log.w(TAG, "PointBuffer NULL");
            return;
        }
        if (nodePosition == null)  {
            Log.w(TAG, "NodePosition null");
            return;
        }
        Log.d(TAG,"Buffer: " + pointBuffer.remaining());
        pointList.addAll(PointFilter.getValidPoints(pointBuffer,nodePosition));
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
        float THRESH = 0.01f;
        if (currentPosition == null) return false;
        boolean xDif = Math.abs(currentPosition.x - storedObjectPosition.x) > THRESH;
        boolean yDif =Math.abs(currentPosition.y - storedObjectPosition.y) > THRESH;
        boolean zDif = Math.abs(currentPosition.z - storedObjectPosition.z) > THRESH;
        if (xDif || yDif || zDif) {
            pointList.clear();
            storedObjectPosition.set(currentPosition);
            Log.d(TAG,"pointList cleared");
            return true;
        }
        return false;
    }

    public float[] getBoxDimLW(Point3F[] boundingBox) {
        float[] inputNullArray = {-1f,-1f};
        if (boundingBox == null || boundingBox.length != 4) return inputNullArray;

        float xOne = boundingBox[0].x;
        float xTwo = boundingBox[3].x;
        float yOne = boundingBox[0].y;
        float yTwo = boundingBox[3].y;

        float sideOne = (float) Math.sqrt(Math.pow(xTwo-xOne,2) + Math.pow(yTwo-yOne,2));

        float xThree = boundingBox[1].x;
        float yThree = boundingBox[1].y;

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
