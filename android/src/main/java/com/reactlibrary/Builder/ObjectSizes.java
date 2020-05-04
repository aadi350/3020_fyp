package com.reactlibrary.Builder;

import android.app.Application;

import com.google.ar.sceneform.math.Vector3;

public  class ObjectSizes extends Application {
    private static final Vector3 CARRY_ON = new Vector3(0.35f,0.23f, 0.56f);
    private static final Vector3 DUFFEL = new Vector3(0.56f,0.23f, 0.35f);
    private static final Vector3 PERSONAL = new Vector3(0.33f,0.15f,0.43f);

    public static Vector3 getPersonal() {
        return PERSONAL;
    }

    public static Vector3 getCarryOn() {
        return CARRY_ON;
    }

    public static Vector3 getDuffel() {
        return DUFFEL;
    }
}
