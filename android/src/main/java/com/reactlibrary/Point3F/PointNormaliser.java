package com.reactlibrary.Point3F;

import com.google.ar.core.PointCloud;
import com.google.ar.core.Pose;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

public class PointNormaliser {
    private Pose oldPose = null, newPose = null;

    public ArrayList<Point3F> normalisePoints(PointCloud pointCloud, Pose androidSensorPose) {
        oldPose = newPose;
        newPose = androidSensorPose;

        FloatBuffer pointBuffer = pointCloud.getPoints();
        IntBuffer pointIDBuffer = pointCloud.getIds();

        ArrayList<Point3F> normalPoints = new ArrayList<>();

        while (pointBuffer.hasRemaining()) {
            normalPoints.add(
                 new Point3F ((pointBuffer.get() - newPose.tx()) + (newPose.tx() - oldPose.tx()),
                    (pointBuffer.get() - newPose.tx()) + (newPose.tx() - oldPose.tx()),
                    (pointBuffer.get() - newPose.tx()) + (newPose.tx() - oldPose.tx()),
                    pointBuffer.get(),
                    pointIDBuffer.get()
                )
            );
        }

        pointBuffer.clear();
        pointIDBuffer.clear();

        return normalPoints;

    }

}
