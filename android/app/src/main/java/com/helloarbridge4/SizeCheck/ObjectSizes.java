package com.helloarbridge4.SizeCheck;

import com.google.ar.sceneform.math.Vector3;

class ObjectSizes {
    //TODO set object sizes
    private static Vector3 personalItemSize = new Vector3();
    private static Vector3 duffelSize = new Vector3();
    private static Vector3 carryOnSize  = new Vector3();

    static Vector3 getPersonalItemSize() {
        return personalItemSize;
    }

    static Vector3 getCarryOnSize() {
        return carryOnSize;
    }

    static Vector3 getDuffelSize() {
        return duffelSize;
    }
}
