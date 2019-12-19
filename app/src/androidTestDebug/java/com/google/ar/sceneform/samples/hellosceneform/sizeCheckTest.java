package com.google.ar.sceneform.samples.hellosceneform;
import  com.google.ar.sceneform.samples.hellosceneform.sizeCheck;

import android.util.Log;
import com.google.ar.sceneform.math.Vector3;
import org.junit.jupiter.api.Test;
import java.nio.FloatBuffer;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.*;

public class sizeCheckTest extends  sizeCheck{

    //Dimensional limits for carry-on object
    private final Vector3 CARRYON_LIM   = new Vector3(0.115f,0.175f,0.56f);

    //Dimensional limits for personal item
    private final Vector3 PERSONAL_LIM  = new Vector3(0.075f,0.165f,0.43f);

    private static final int COUNT_THRESH = 9999;
    private Vector3 objectAnchor = new Vector3(0.0f,0.0f,0.0f);
    private sizeCheck sizeCheckObject = new sizeCheck();
    private FloatBuffer pointCloud =  null;

    private final float X_UPPER = 0.175f;
    private final float Y_UPPER = 0.115f;
    private final float Z_UPPER = 0.56f;

    public sizeCheckTest(boolean type)
    {

    }

    @Test
    void loadPoints()
    {
        sizeCheckObject.loadPointsFromFloatBuffer(this.pointCloud);
        assertNotNull(sizeCheckObject.getPointsFromFloatBuffer());
    }

    @Test
    void chooseObjectType()
    {
        sizeCheckObject.setObjectType(false);
        assertFalse(sizeCheckObject.getBagType());
        sizeCheckObject.setObjectType(true);
        assertTrue(sizeCheckObject.getBagType());
    }

    @Test
    void checkObjectLimitsByType()
    {
        sizeCheckObject.setObjectType(false);
        assertEquals(
                sizeCheckObject.getLimits(),
                CARRYON_LIM,
                "sizeCheck Carry On"
        );

        sizeCheckObject.setObjectType(true);
        assertEquals(
                sizeCheckObject.getLimits(),
                PERSONAL_LIM,
                "sizeCheck Personal Item"
        );
    }

    @Test
    void testSetAnchor()
    {
        sizeCheckObject.setObjectAnchor(objectAnchor);
        assertNotNull(sizeCheckObject.getAnchor());
    }



}