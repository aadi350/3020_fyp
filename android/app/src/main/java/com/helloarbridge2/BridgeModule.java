package com.helloarbridge2;

import android.content.Intent;
import android.os.Bundle;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.uimanager.IllegalViewOperationException;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class BridgeModule extends ReactContextBaseJavaModule{
    public BridgeModule(ReactApplicationContext reactContext) {
        super(reactContext); //required by React Native
    }

    @Override
    //getName is required to define the name of the module represented in JavaScript
    public String getName() {
        return "Bridge";
    }

    @ReactMethod
    public void launchNative(Callback errorCallback, Callback successCallback) {
        try {
            System.out.println("JavaClass:Bridge Module, Method: launchNative");
            successCallback.invoke("Class: BridgeModule - launchNative");
        } catch (IllegalViewOperationException e) {
            errorCallback.invoke(e.getMessage());
        }

        //launch Activity via Intent
        ReactApplicationContext context = getReactApplicationContext();
        Intent intent = new Intent(context, ARActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }




}
