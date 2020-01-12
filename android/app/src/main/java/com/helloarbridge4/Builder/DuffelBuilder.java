package com.helloarbridge4.Builder;

import android.content.Context;

import com.google.ar.sceneform.math.Vector3;

public class DuffelBuilder extends objectBuilder {
    public void initBuilder(Context context) {
        final float LENGTH = 0.56f;
        final float WIDTH = 0.35f;
        final float HEIGHT = 0.23f;

        SFBRed = "duffel_red.sfb";
        SFBGreen = "duffel_green.sfb";
        SFBNeutral = "duffel.sfb";

        objectSize = new Vector3(LENGTH,WIDTH,HEIGHT);

        this.context = context;
        buildAll();

    }
}
