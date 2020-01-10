package com.helloarbridge4;

import android.content.Context;

public class PersonalItemBuilder extends objectBuilder {
    public void initBuilder(Context context) {
        SFBRed = "personalItem_red.sfb";
        SFBGreen = "personalItem_green.sfb";
        SFBNeutral = "personalItem.sfb";


        this.context = context;
    }
}
