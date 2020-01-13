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
    private objectBuilder carryOnBuilder = new CarryOnBuilder();
    private objectBuilder duffelBuilder = new DuffelBuilder();
    private objectBuilder personalItemBuilder = new PersonalItemBuilder();
    private TransformableNode transformableNode;
    private AnchorNode anchorNode;

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

    public void setCarryOnNeutral() {
        if (anchorNode == null ||  transformableNode == null) {
            return;
        }
        carryOnBuilder.getNeutral().select(anchorNode,transformableNode);
    }

    public void setDuffelNeutral() {
        if (anchorNode == null ||  transformableNode == null) {
            return;
        }
        duffelBuilder.getNeutral().select(anchorNode, transformableNode);
    }

    public void setPersonalItemNeutral() {
        if (anchorNode == null ||  transformableNode == null) {
            return;
        }
        personalItemBuilder.getNeutral().select(anchorNode,transformableNode);
    }

    public void setCarryOnLarge() {
        if (anchorNode == null ||  transformableNode == null) {
            return;
        }
        carryOnBuilder.getLarge().select(anchorNode,transformableNode);
    }

    public void setDuffelLarge() {
        if (anchorNode == null ||  transformableNode == null) {
            return;
        }
        duffelBuilder.getLarge().select(anchorNode,transformableNode);
    }

    public void setPersonalItemLarge() {
        if (anchorNode == null ||  transformableNode == null) {
            return;
        }
        personalItemBuilder.getLarge().select(anchorNode,transformableNode);
    }

    public void setCarryOnFits() {
        if (anchorNode == null ||  transformableNode == null) {
            return;
        }
        carryOnBuilder.getFits().select(anchorNode,transformableNode);
    }

    public void setDuffelFits() {
        if (anchorNode == null ||  transformableNode == null) {
            return;
        }
        duffelBuilder.getFits().select(anchorNode,transformableNode);
    }

    public void setPersonalItemFits() {
        if (anchorNode == null ||  transformableNode == null) {
            return;
        }
        personalItemBuilder.getFits().select(anchorNode,transformableNode);
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
