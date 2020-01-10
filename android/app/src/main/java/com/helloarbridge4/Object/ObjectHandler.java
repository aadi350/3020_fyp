package com.helloarbridge4.Object;

import android.content.Context;

import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.ux.TransformableNode;
import com.helloarbridge4.Builder.CarryOnBuilder;
import com.helloarbridge4.Builder.DuffelBuilder;
import com.helloarbridge4.Builder.PersonalItemBuilder;
import com.helloarbridge4.Builder.objectBuilder;

public class ObjectHandler {

    //Builders
    objectBuilder carryOnBuilder = new CarryOnBuilder();
    objectBuilder duffelBuilder = new DuffelBuilder();
    objectBuilder personalItemBuilder = new PersonalItemBuilder();



    public ObjectHandler(Context context) {
        carryOnBuilder.initBuilder(context);
        duffelBuilder.initBuilder(context);
        personalItemBuilder.initBuilder(context);
    }

    public void setCarryOnNeutral(AnchorNode anchorNode, TransformableNode transformableNode) {
        if (anchorNode == null ||  transformableNode == null) {
            return;
        }
        carryOnBuilder.getNeutral().select(anchorNode,transformableNode);
    }

    public void setDuffelNeutral(AnchorNode anchorNode, TransformableNode transformableNode) {
        duffelBuilder.getNeutral().select(anchorNode, transformableNode);
    }

    public void setPersonalItemNeutral(AnchorNode anchorNode, TransformableNode transformableNode) {
        personalItemBuilder.getNeutral().select(anchorNode,transformableNode);
    }

    public void removeAll(TransformableNode node) {
        if (node == null) {
            return;
        }
        carryOnBuilder.getNeutral().unSelect(node);
        duffelBuilder.getNeutral().unSelect(node);
        personalItemBuilder.getNeutral().unSelect(node);

        carryOnBuilder.getFits().unSelect(node);
        duffelBuilder.getFits().unSelect(node);
        personalItemBuilder.getFits().unSelect(node);

        carryOnBuilder.getLarge().unSelect(node);
        duffelBuilder.getLarge().unSelect(node);
        personalItemBuilder.getLarge().unSelect(node);
    }



}
