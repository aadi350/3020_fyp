package com.helloarbridge4.Builder;

import android.content.Context;

public class CarryOnBuilder extends objectBuilder {
    public void initBuilder(Context context) {
        SFBRed = "suitcase_red.sfb";
        SFBGreen = "suitcase_green.sfb";
        SFBNeutral = "suitcase.sfb";


        this.context = context;
        buildAll();
    }
}
