package com.reactlibrary.ColourChange;

import android.content.Context;

import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.ux.TransformableNode;
import com.reactlibrary.Object.CarryOnHandler;
import com.reactlibrary.Object.DuffelHandler;
import com.reactlibrary.Object.ObjectCodes;
import com.reactlibrary.Object.ObjectHandler;
import com.reactlibrary.Object.PersonalItemHandler;
import com.reactlibrary.FitCodes;

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
