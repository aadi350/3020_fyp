package com.helloarbridge4.SizeCheck;

import android.util.Log;

import com.google.ar.core.PointCloud;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.ux.TransformableNode;
import com.helloarbridge4.Builder.CarryOnBuilder;
import com.helloarbridge4.Builder.DuffelBuilder;
import com.helloarbridge4.Builder.PersonalItemBuilder;
import com.helloarbridge4.Object.ObjectCodes;
import com.helloarbridge4.Point3F.Point3F;
import com.helloarbridge4.Point3F.PointFilter;

import java.nio.FloatBuffer;
import java.util.ArrayList;

public class SizeCheckHandler {
    private final int POINT_LOWER_THRESH = 25;
    private final String TAG = "SizeCheckHandler";
    private Vector3 objectSize = new Vector3();
    private Vector3 nodePosition;
    private Point3F[] boundingBox;
    private ArrayList<Point3F> pointList = new ArrayList<>();
    private float highZ;


    public FitCodes checkIfFits(ObjectCodes objectCode, TransformableNode node, PointCloud pointCloud) {
        if (pointCloud == null) return null;
        Log.d(TAG, "checkIfFits()");
        QuickSort q = new QuickSort();
        QuickHull c = new QuickHull();

        Vector3 actualSize = Vector3.zero();
        Vector3 objectPosition = loadObjectPosition(node);
        FloatBuffer pointBuffer = loadPointCloud(pointCloud);
        if (!pointBuffer.hasRemaining()) return null;
        getValidPoints(pointBuffer, objectPosition);

        if (pointList.size() < POINT_LOWER_THRESH) return null;

        try {
            boundingBox = TwoDimensionalOrientedBoundingBox.getMinimumBoundingRectangle(pointList);
            highZ = q.getHighestZ(this.pointList) - node.getWorldPosition().z;
            Log.d(TAG,"HighZ: " + highZ);
            Log.d(TAG, "Corners: " + boundingBox[0] + " ");
            Log.d(TAG, "Corners: " + boundingBox[1] + " ");
            Log.d(TAG, "Corners: " + boundingBox[2] + " ");
            Log.d(TAG, "Corners: " + boundingBox[3] + " ");

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

        } catch (IllegalArgumentException i ) {
            Log.w(TAG, i.getLocalizedMessage());
        } catch (ArrayIndexOutOfBoundsException a) {
            Log.w(TAG, a.getLocalizedMessage());
        } catch (IndexOutOfBoundsException b) {
            Log.w(TAG, b.getLocalizedMessage());
        } catch (Exception e) {
            Log.w(TAG,e.getLocalizedMessage());
        }

        if (actualSize.equals(Vector3.zero())) return null;


        boolean fits = compareLimits(objectSize, actualSize);
        FitCodes fitCode = (fits) ? FitCodes.FIT : FitCodes.LARGE;
        return fitCode;
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

    public FloatBuffer loadPointCloud(PointCloud pointCloud) {
        if (pointCloud == null) return FloatBuffer.allocate(0);
        FloatBuffer pointBuffer = pointCloud.getPoints();
        Log.d(TAG, "pointCloud loaded, PointBuffer NULL: " + (pointBuffer == null));
        return pointBuffer;
    }

    public Vector3 loadObjectPosition(TransformableNode node) {
        Log.d(TAG,"Pos: " + node.getWorldPosition().toString());
        return node.getWorldPosition();
    }


    public float getBoxLength() {
        return getBoxDimLW(this.boundingBox)[0];
    }

    public float getBoxWidth() {
        return getBoxDimLW(this.boundingBox)[1];
    }

    public float getHighZ() {
        if (this.highZ < 0f) return -1f;
        return this.highZ;
    }


    private float[] getBoxDimLW(Point3F[] boundingBox) {
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
