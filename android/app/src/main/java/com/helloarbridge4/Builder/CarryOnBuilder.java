package com.helloarbridge4.Builder;

import android.content.Context;

import com.google.ar.sceneform.math.Vector3;

public class CarryOnBuilder extends objectBuilder {
    public void initBuilder(Context context) {
        final float LENGTH = 0.35f;
        final float WIDTH = 0.23f;
        final float HEIGHT = 0.56f;


        SFBRed = "suitcase_red.sfb";
        SFBGreen = "suitcase_green.sfb";
        SFBNeutral = "suitcase.sfb";

        objectSize = new Vector3(LENGTH,WIDTH,HEIGHT);

        this.context = context;
        buildAll();
    }
}
