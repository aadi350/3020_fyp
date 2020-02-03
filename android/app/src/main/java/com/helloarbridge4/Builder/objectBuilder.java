package com.helloarbridge4.Builder;

import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;

import com.google.ar.sceneform.math.Vector3;
import com.helloarbridge4.Object.SceneFormObject;

public abstract class objectBuilder extends AppCompatActivity {
    protected Context context;
    protected String SFBRed, SFBGreen, SFBNeutral;
    protected Vector3 objectSize;

    protected SceneFormObject objectLarge, objectFits, objectNeutral;

    public abstract void initBuilder(Context context);

    public void buildAll() {
        //call only within initBuilder
        buildGreen();
        buildNeutral();
        buildRed();
    }


    public SceneFormObject getFits() {
        return this.objectFits;
    }

    public SceneFormObject getLarge() {
        return this.objectLarge;
    }

    public SceneFormObject getNeutral() {
        return this.objectNeutral;
    }

    protected void buildRed() {
        this.objectLarge = new SceneFormObject(this.context, SFBRed);
        this.objectLarge.setObjectLimits(objectSize);
    }

    protected void buildGreen() {
        this.objectFits = new SceneFormObject(this.context, SFBGreen);
        this.objectFits.setObjectLimits(objectSize);
    }

    protected void buildNeutral() {
        this.objectNeutral = new SceneFormObject(this.context, SFBNeutral);
        this.objectNeutral.setObjectLimits(objectSize);
    }

    public Vector3 getObjectSize() {
        return  this.objectSize;
    }
}
