package com.helloarbridge4;

import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;

public abstract class objectBuilder extends AppCompatActivity {
    protected Context context;
    protected String[] SFB;

    //TO-DO
    //implement SceneFormObject class
    //protected SceneFormObject object;

    public abstract void initBuilder(Context context);

//    public SceneFormObject getObject() {
//        return this.object;
//    }
}
