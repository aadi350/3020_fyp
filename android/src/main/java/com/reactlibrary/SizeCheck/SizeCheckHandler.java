package com.reactlibrary.SizeCheck;

import android.icu.util.Freezable;
import android.util.Log;


import com.google.ar.core.PointCloud;
import com.google.ar.core.Pose;
import com.google.ar.core.TrackingState;
import com.google.ar.sceneform.math.Vector3;
import com.reactlibrary.Builder.CarryOnBuilder;
import com.reactlibrary.Builder.DuffelBuilder;
import com.reactlibrary.Builder.ObjectSizes;
import com.reactlibrary.Builder.PersonalItemBuilder;
import com.reactlibrary.FitCodes;
import com.reactlibrary.Object.ObjectCodes;
import com.reactlibrary.Point3F.Point3F;
import com.reactlibrary.Point3F.PointFilter;
import com.reactlibrary.SizeCheck.MinBoundingBox.QuickHull;
import com.reactlibrary.SizeCheck.MinBoundingBox.Rectangle;
import com.reactlibrary.SizeCheck.MinBoundingBox.TwoDimensionalOrientedBoundingBox;

import java.nio.FloatBuffer;
import java.util.ArrayList;



public class SizeCheckHandler {

    private final String TAG = "SizeCheckHandler";
    private Vector3 objectSize = new Vector3();
    private ArrayList<Point3F> pointList = new ArrayList<>();
    private Vector3 anchorNodePosition;
    private ObjectCodes objectCode;
    private Vector3 actualSize = new Vector3(Vector3.zero());
    private Vector3 NULL_VECTOR = new Vector3(-1f,-1f,-1f);
    private FitCodes fitCode;

    public void updateAnchor(Vector3 anchorNodePosition) {
        this.anchorNodePosition = anchorNodePosition;
        Log.i(TAG,"updateAnchor()");
    }

    public void loadPointList(ArrayList<Point3F> pointList) {
        Log.d(TAG,"loadPointList()");
        this.pointList = PointFilter.filterPoints(pointList,anchorNodePosition);
    }

    public void loadPointCloud(PointCloud pointCloud) {
        ArrayList<Point3F> tempList =  PointFilter.convertCloudToArrayList(pointCloud);
        this.pointList.addAll(PointFilter.filterPoints(tempList,anchorNodePosition));
        Log.i(TAG,"loadPointCloud(): " + pointList.size());
    }

    public void setObject(ObjectCodes objectCode) {
        Log.d(TAG,"setObject");
        this.objectCode = objectCode;
        switch (objectCode) {
            case DUFFEL:
                objectSize = ObjectSizes.getDuffel();
                break;
            case CARRYON:
                objectSize = ObjectSizes.getCarryOn();
                break;
            case PERSONAL:
                objectSize = ObjectSizes.getPersonal();
                break;
        }
    }

    public ObjectCodes getObjectType() {
        return this.objectCode;
    }

    public Vector3 setActualSize(ArrayList<Point3F> pointList) {
        Log.d(TAG,"setActualSize");
        if (pointList.size() < 50) return NULL_VECTOR;
        try {
            float[] boxDim = calcBox(pointList);
            float height = getHighPointVal(pointList) + anchorNodePosition.y;

            return new Vector3(
                    boxDim[0],
                    boxDim[1],
                    height
            );
        } catch (NullPointerException e) {
            return NULL_VECTOR;
        }
    }

    public Vector3 getBoxDim() {
        return setActualSize(pointList);
    }

    public FitCodes checkIfFits() {
        Log.d(TAG,"checkIfFits");
        Vector3 calcSize = setActualSize(pointList);

        if (calcSize.equals(NULL_VECTOR) ) {
            Log.d(TAG,"calcSize null");
            this.fitCode = FitCodes.NONE;
            return fitCode;
        }
        Log.d(TAG, calcSize.toString());

        actualSize = calcSize;
       try {
           Log.d(TAG,"compareLimits");
           return compareLimits(
                   objectSize,
                   actualSize
           );
       } catch (NullPointerException e){
           this.fitCode = FitCodes.NONE;
           return this.fitCode;
       }
    }

    private float[] calcBox(ArrayList<Point3F> pointList) throws NullPointerException {
        Log.d(TAG,"calcBox");
        float[] boxDim;
        if (pointList == null) throw new NullPointerException("PointList null");
        Rectangle boundingBox = TwoDimensionalOrientedBoundingBox.getOBB(pointList);

        assert boundingBox != null;
        if (boundingBox.height < boundingBox.width) {
            boxDim = new float[] {
                    (float) boundingBox.width,
                    (float) boundingBox.height
            };
        } else {
            boxDim = new float[] {
                    (float) boundingBox.height,
                    (float) boundingBox.width
            };
        }
        return boxDim;
    }

    private float getHighPointVal(ArrayList<Point3F> pointList) {
        QuickSort q = new QuickSort();
        return q.getHighestPoint(pointList);
    }


    public FitCodes compareLimits(Vector3 ref, Vector3 actual) throws NullPointerException{
        Log.d(TAG,"compareLimits");
        if (ref == null) throw new NullPointerException("compareLimits: Object reference null");

        FitCodes fits =
                (       (ref.x >= actual.x) &&
                        (ref.y >= actual.y) &&
                        (ref.z >= actual.z)) ?
                        FitCodes.FIT : FitCodes.LARGE;
        Log.d(TAG,fits.toString());
        return fits;
    }

    public ArrayList<Point3F> getPointList() {
        return this.pointList;
    }

}