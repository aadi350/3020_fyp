package com.helloarbridge4.Object;

import android.content.Context;
public class DuffelHandler extends ObjectHandler{

    public DuffelHandler(Context context) {
        super(context);
    }

    public void setNeutral() {
        if (anchorNode == null ||  transformableNode == null) {
            return;
        }
        duffelBuilder.getNeutral().select(anchorNode, transformableNode);
    }

    public void setFits() {
        if (anchorNode == null ||  transformableNode == null) {
            return;
        }
        duffelBuilder.getFits().select(anchorNode,transformableNode);
    }

    public void setLarge() {
        if (anchorNode == null ||  transformableNode == null) {
            return;
        }
        duffelBuilder.getLarge().select(anchorNode,transformableNode);
    }


}
