package com.hellobridge;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.uimanager.IllegalViewOperationException;

public class HelloWorldModule extends ReactContextBaseJavaModule {
    public HelloWorldModule(ReactApplicationContext reactContext) {
        super(reactContext); //required by React Native
    }

    @Override
    //getName is required to define the name of the module represented in JavaScript
    public String getName() {
        return "HelloWorld";
    }

    @ReactMethod
    public void sayHi(Callback errorCallback, Callback successCallback) {
        try {
            System.out.println("Native Method Invoked");
            successCallback.invoke("Callback : Native Method Invoked");
        } catch (IllegalViewOperationException e) {
            errorCallback.invoke(e.getMessage());
        }
    }
}
