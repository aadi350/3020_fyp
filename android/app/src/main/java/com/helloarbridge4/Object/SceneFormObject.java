package com.helloarbridge4.Object;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.TransformableNode;

public class SceneFormObject extends AppCompatActivity {
//    private static final float OBJ_DIAGONAL =  1.5625f;
    private ModelRenderable modelRenderable;
    private TransformableNode node;
    private final String TAG = "SceneFormObject";

    private Vector3 objectRegionLimts;
    private Vector3 objectSize;


    public SceneFormObject(Context context, String SfbString) {
        ModelRenderable.builder()
                .setSource(context, Uri.parse(SfbString))
                .build()
                .thenAccept(renderable -> modelRenderable = renderable)
                .exceptionally(
                        throwable -> {
                            Log.e(TAG, "Unable to load Renderable.", throwable);
                            return null;
                        });
    }

    public void setObjectLimits(Vector3 objectSize, Vector3 objectRegionLimts) {
        this.objectRegionLimts = objectRegionLimts;
        this.objectSize = objectSize;
    }

    public void select(AnchorNode anchorNode, TransformableNode node) {
        try {
            modelRenderable.setShadowCaster(false);
            node.getScaleController().setEnabled(false);
            node.setRenderable(modelRenderable);
            node.setParent(anchorNode);
            node.select();
        } catch (NullPointerException e) {
            Log.e(TAG, "select()" + e.getMessage());
        }
    }

    public void unSelect(TransformableNode node) {
        try {
            node.setRenderable(null);
            node.setParent(null);
            Log.d(TAG, "unSelect()");
        } catch (NullPointerException e) {
            Log.e(TAG, "unSelect: " + e.getMessage());
        }
    }

}
