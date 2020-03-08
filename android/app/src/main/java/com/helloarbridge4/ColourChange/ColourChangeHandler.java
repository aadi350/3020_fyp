package com.helloarbridge4.ColourChange;

import android.content.Context;

import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.ux.TransformableNode;
import com.helloarbridge4.Object.CarryOnHandler;
import com.helloarbridge4.Object.DuffelHandler;
import com.helloarbridge4.Object.ObjectCodes;
import com.helloarbridge4.Object.ObjectHandler;
import com.helloarbridge4.Object.PersonalItemHandler;
import com.helloarbridge4.FitCodes;

public class ColourChangeHandler {
    private ObjectCodes currentObject;
    private ObjectHandler carryOnHandler;
    private ObjectHandler duffelHandler;
    private ObjectHandler personalItemHandler;

    public ColourChangeHandler(Context context) {
        carryOnHandler = new CarryOnHandler(context);
        duffelHandler = new DuffelHandler(context);
        personalItemHandler = new PersonalItemHandler(context);
    }

    public void setTransformableNode(TransformableNode transformableNode) {
        if (transformableNode == null) return;

        carryOnHandler.setTransformableNode(transformableNode);
        duffelHandler.setTransformableNode(transformableNode);
        personalItemHandler.setTransformableNode(transformableNode);
    }

    public void setAnchorNode(AnchorNode anchorNode) {
        if (anchorNode == null) return;

        carryOnHandler.setAnchorNode(anchorNode);
        duffelHandler.setAnchorNode(anchorNode);
        personalItemHandler.setAnchorNode(anchorNode);
    }



    public void updateObject(ObjectCodes objectCode) {
        if (objectCode != currentObject) {
            currentObject = objectCode;
        }
    }

    public void setObject(FitCodes fitCode) {
        if (fitCode == null) return;
        switch (currentObject) {
            case CARRYON:
                carryOnHandler.setColor(fitCode);
                break;
            case PERSONAL:
                personalItemHandler.setColor(fitCode);
                break;
            case DUFFEL:
                duffelHandler.setColor(fitCode);
                break;
        }
    }





}
