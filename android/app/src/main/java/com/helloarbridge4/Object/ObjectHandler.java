package com.helloarbridge4.Object;

import android.content.Context;

import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.ux.TransformableNode;
import com.helloarbridge4.Builder.CarryOnBuilder;
import com.helloarbridge4.Builder.DuffelBuilder;
import com.helloarbridge4.Builder.PersonalItemBuilder;
import com.helloarbridge4.Builder.objectBuilder;
import com.helloarbridge4.SizeCheck.FitCodes;

public abstract class ObjectHandler {

    //Builders
    protected objectBuilder carryOnBuilder = new CarryOnBuilder();
    protected objectBuilder duffelBuilder = new DuffelBuilder();
    protected objectBuilder personalItemBuilder = new PersonalItemBuilder();
    protected TransformableNode transformableNode;
    protected AnchorNode anchorNode;

    public ObjectHandler() {}

    public abstract void setNeutral();
    public abstract void setFits();
    public abstract void setLarge();

    public void setColor(FitCodes fitCode) {
        switch (fitCode) {
            case LARGE:
                setLarge();
                break;
            case FIT:
                setFits();
                break;
            default:
                setNeutral();
                break;
        }
    }


    public ObjectHandler(Context context) {
        carryOnBuilder.initBuilder(context);
        duffelBuilder.initBuilder(context);
        personalItemBuilder.initBuilder(context);
    }

    public void setTransformableNode(TransformableNode transformableNode) {
        if (transformableNode == null) return;
        this.transformableNode = transformableNode;
    }

    public void setAnchorNode(AnchorNode anchorNode) {
        this.anchorNode = anchorNode;
    }

}
