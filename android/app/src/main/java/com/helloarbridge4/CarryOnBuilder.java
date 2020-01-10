package com.helloarbridge4;

import android.content.Context;

public class CarryOnBuilder extends objectBuilder {
    public void initBuilder(Context context) {
        SFBRed = "suitcase_red.sfb";
        SFBGreen = "suitcase_green.sfb";
        SFBNeutral = "suitcase.sfb";


        this.context = context;
    }

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
