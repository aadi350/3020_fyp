package com.helloarbridge4;

import android.content.Context;

public class DuffelBuilder extends objectBuilder {
    public void initBuilder(Context context) {
        SFBRed = "duffel_red.sfb";
        SFBGreen = "duffel_green.sfb";
        SFBNeutral = "duffel.sfb";


        this.context = context;
    }
}
