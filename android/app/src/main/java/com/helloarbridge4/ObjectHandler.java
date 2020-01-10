package com.helloarbridge4;

import android.content.Context;

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

    public SceneFormObject getCarryOnNeutral() {
        return carryOnBuilder.getNeutral();
    }

    public SceneFormObject getDuffelNeutral() {
        return duffelBuilder.getNeutral();
    }

    public SceneFormObject getPersonalItemNeutral() {
        return personalItemBuilder.getNeutral();
    }



}
