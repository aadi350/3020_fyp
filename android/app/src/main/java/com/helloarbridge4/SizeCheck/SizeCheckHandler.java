package com.helloarbridge4.SizeCheck;

import com.google.ar.core.PointCloud;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.ux.TransformableNode;
import com.helloarbridge4.ObjectCodes;

public class SizeCheckHandler {
    ObjectCodes objectCodes;
    PointCloud pointCloud;
    Quaternion objectRotation;
    TransformableNode objectCentre;

    public void loadPointCloud(PointCloud pointCloud) {
        this.pointCloud = pointCloud;
    }

    public void setObjectCentre(TransformableNode transformableNode) {
        this.objectCentre = transformableNode;
        this.objectRotation = objectCentre.getWorldRotation();
    }


}
