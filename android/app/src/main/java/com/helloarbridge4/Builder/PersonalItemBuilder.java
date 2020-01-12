package com.helloarbridge4.Builder;

import android.content.Context;

import com.google.ar.sceneform.math.Vector3;

public class PersonalItemBuilder extends objectBuilder {
    public void initBuilder(Context context) {
        final float LENGTH = 0.33f;
        final float WIDTH = 0.15f;
        final float HEIGHT = 0.43f;

        SFBRed = "personalItem_red.sfb";
        SFBGreen = "personalItem_green.sfb";
        SFBNeutral = "personalItem.sfb";

        objectSize = new Vector3(LENGTH,WIDTH,HEIGHT);

        this.context = context;
        buildAll();
    }
}
