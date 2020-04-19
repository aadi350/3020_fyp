package com.reactlibrary;

import android.app.Application;
import android.content.Intent;


public class ARApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Intent i = new Intent(this, ARActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
