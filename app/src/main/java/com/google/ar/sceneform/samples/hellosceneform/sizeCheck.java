package com.google.ar.sceneform.samples.hellosceneform;

import com.google.ar.core.PointCloud;
import com.google.ar.sceneform.math.Vector3;

import java.nio.FloatBuffer;

public class sizeCheck {
    //master boolean
    private boolean fits;

    //struct for instantaneous point in PointCloud
    private Vector3 vector_pc = new Vector3(0.00f, 0.00f, 0.00f);

    //Constants
    //Maximum and Minimum distances from anchor to register as oversized
    //distances halfed
    private static final Vector3 MAX = new Vector3(0.315f, 0.375f, 0.48f);
    private static final Vector3 MIN = new Vector3(0.115f, 0.175f, 0.28f);
    //used to exclude plane from point detection
    private static final float Z_THRESH = 0.04f;
    //used to set relative limits from centre of placed object
    private Vector3 max;
    private Vector3 min;

    //to hold point cloud
    private FloatBuffer floatBuffer;



    public boolean objectFits(FloatBuffer pointsBuffer, Vector3 anchor)
    {
        //Master method
        fits = false;
        /*-----------------------------------------------------------------------------------------*/
        //preliminary debugging
        fits = boundingbox(pointsBuffer, anchor);
        /*-----------------------------------------------------------------------------------------*/
        return fits;

    }

    //for debugging
    private boolean boundingbox (FloatBuffer pointsBuffer, Vector3 anchor)
    {
        //boolean for 3-directions
        boolean fits_x, fits_y, fits_z;

        //Delineate points from floatbuffer
        Float[] points = new Float[3];
        points[0] = pointsBuffer.get();     //x
        points[1] = pointsBuffer.get();     //y
        points[2] = pointsBuffer.get();     //z

        //define coordinates of maximum object boundary
        //set boundaries for x
        float x_up      = anchor.x + MAX.x;
        float x_down    = anchor.x - MAX.x;
        //set boundaries for y
        float y_up      = anchor.y + MAX.x;
        float y_down    = anchor.y - MAX.x;

        //set boundary for z
        float z_up        =  anchor.z + MAX.z;

        fits_x = (points[0] > x_down && points[0] < x_up);
        fits_y = (points[1] > y_down && points[1] < y_up);
        fits_z = (points[2] < z_up && points[2] > Z_THRESH);

        return (fits_x & fits_y & fits_z);
    }


}
