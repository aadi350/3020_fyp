package com.google.ar.sceneform.samples.hellosceneform;

import android.util.Log;

import com.google.ar.sceneform.math.Vector3;

import java.nio.BufferUnderflowException;
import java.nio.FloatBuffer;

class sizeCheck {

    private boolean objectDetected      = false;
    //false if exceeds
    private boolean objectWithinBounds  = false;

    private final float Z_THRESH = 0.15f;
    //Dimensional limits for carry-on object
    private final Vector3 CARRYON_LIM   = new Vector3(0.115f,0.175f,0.56f);
    private final Vector3 CARRYON_SIZE = new Vector3(0.23f,0.35f,0.56f);
    private final Vector3 CARRYON_BOUND = CARRYON_LIM;//new Vector3(1.0f,1.0f,1.0f);

    //Dimensional limits for personal item
    private final Vector3 PERSONAL_LIM  = new Vector3(0.075f,0.165f,0.43f);
    private final Vector3 PERSONAL_SIZE  = new Vector3(0.15f,0.33f,0.43f);

    private Vector3 objectSizeLimits = Vector3.zero();
    private Vector3 objectCorner   = Vector3.zero();
    private Vector3 objectSizeActual = Vector3.zero();
    private Vector3 objectLocation = Vector3.zero();
    private Vector3 pointLocationRelativeToCorner = Vector3.zero();
    private Vector3 pointLocationAbsolute = Vector3.zero();
    private boolean bagTypeFalseIfCarryon = false;

    private FloatBuffer pointBuffer;


    //sets type of carry-on item by input argument
    public void setObjectType(boolean type)
    {
        this.bagTypeFalseIfCarryon = type;
        Log.d("setObjectType", Boolean.toString(bagTypeFalseIfCarryon));
    }

    public void setObjectAnchor(Vector3 anchor)
    {
        try {
            objectLocation = anchor;
            Log.d("setObjectAnchor",Float.toString(anchor.x) + Float.toString(anchor.y) + Float.toString(anchor.z));
        } catch (Exception e)
        {
            Log.e("setObjectAnchor",e.getMessage());
        }
    }




    //default constructor for JUnit 5
    public sizeCheck() {

    }


    public void loadPointsFromFloatBuffer(FloatBuffer points)
    {
        try{
            this.pointBuffer = points;
            Log.i("loadPointsFromFloatBuffer",": Load Success");
        } catch (Exception e)
        {
            Log.e("loadPointsFromFloatBuffer",e.getMessage());
        }
    }

    public void comparePointsToLimits()
    {
        try {
            do {
                pointLocationAbsolute.set(
                        pointBuffer.get(),
                        pointBuffer.get(),
                        pointBuffer.get()
                );

                pointLocationRelativeToCorner = Vector3.subtract(pointLocationAbsolute,objectCorner);

                objectDetected = ifUpperGreaterThanLower(CARRYON_BOUND, pointLocationRelativeToCorner);

//                objectWithinBounds = (
//                    ifUpperGreaterThanLower(pointLocationAbsolute, objectCorner) &&
//                    ifUpperGreaterThanLower(CARRYON_SIZE, pointLocationRelativeToCorner)
//                );

            } while(pointBuffer.hasRemaining());
        } catch (BufferUnderflowException e)
        {
            Log.e("comparePointsToLimits","Buffer UnderFlow");
        }
    }

    public int ifObjectFits()
    {
        try {
            if (objectDetected && objectWithinBounds) {
                return 2;
            }
            if (objectDetected && !objectWithinBounds) {
                return 1;
            }
            if (!objectDetected) {
                return 0;
            }
            else {
                return 0;
            }
        } catch (NullPointerException n) {
            Log.e("ifObjectFits","Return Status failed: " + n.getMessage());
        }
        return 0;
    }

    private boolean ifUpperGreaterThanLower(Vector3 upper, Vector3 lower)
    {
        return (
                        (upper.x >= lower.x) &&
                        (upper.y >= lower.y) &&
                        (upper.z >= lower.z + Z_THRESH)
                );
    }

    public void setObjectSizeLimits()
    {
        objectSizeLimits = (bagTypeFalseIfCarryon) ? PERSONAL_LIM : CARRYON_LIM;
        objectCorner.set(
                objectLocation.x - objectSizeLimits.x,
                objectLocation.y - objectSizeLimits.y,
                objectLocation.z
                );
    }




    //getter methods for testing
    public FloatBuffer getPointsFromFloatBuffer()
    {
        return this.pointBuffer;
    }

    public boolean getBagType()
    {
        return this.bagTypeFalseIfCarryon;
    }

    public Vector3 getLimits()
    {
        return objectSizeLimits;
    }

    public Vector3 getAnchor()
    {
        return objectLocation;
    }
}
