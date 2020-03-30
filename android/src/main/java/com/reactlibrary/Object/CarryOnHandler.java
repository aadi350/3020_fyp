package com.reactlibrary.Object;

import android.content.Context;

public class CarryOnHandler extends ObjectHandler{
    public CarryOnHandler(Context context) {
        super(context);
    }

    public void setNeutral() {
        if (anchorNode == null ||  transformableNode == null) {
            return;
        }
        carryOnBuilder.getNeutral().select(anchorNode, transformableNode);
    }

    public void setFits() {
        if (anchorNode == null ||  transformableNode == null) {
            return;
        }
        carryOnBuilder.getFits().select(anchorNode,transformableNode);
    }

    public void setLarge() {
        if (anchorNode == null ||  transformableNode == null) {
            return;
        }
        carryOnBuilder.getLarge().select(anchorNode,transformableNode);
    }
}
