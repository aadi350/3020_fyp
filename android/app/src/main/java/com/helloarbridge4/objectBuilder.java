package com.helloarbridge4;

import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;

public abstract class objectBuilder extends AppCompatActivity {
    protected Context context;
    protected String SFBRed, SFBGreen, SFBNeutral;


    //TO-DO
    //implement ObjectBuilder classes
    protected SceneFormObject object;

    public abstract void initBuilder(Context context);

//    public abstract SceneFormObject getFits();
//    public abstract SceneFormObject getLarge();
//    public abstract SceneFormObject getNeutral();
//
//    protected abstract void buildRed();
//    protected abstract void buildNeutral();
//    protected abstract void buildGreen();

    public SceneFormObject getFits() {
        buildGreen();
        return this.object;
    }

    public SceneFormObject getLarge() {
        buildRed();
        return this.object;
    }

    public SceneFormObject getNeutral() {
        buildNeutral();
        return this.object;
    }

    protected void buildRed() {
        this.object = new SceneFormObject(this.context, SFBRed);
    }

    protected void buildGreen() {
        this.object = new SceneFormObject(this.context, SFBGreen);
    }

    protected void buildNeutral() {
        this.object = new SceneFormObject(this.context, SFBNeutral);
    }
}
