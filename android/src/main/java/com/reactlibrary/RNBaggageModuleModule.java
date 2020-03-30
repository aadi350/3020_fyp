
package com.reactlibrary;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import com.facebook.react.uimanager.IllegalViewOperationException;

import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class RNBaggageModuleModule extends ReactContextBaseJavaModule {

  public RNBaggageModuleModule(ReactApplicationContext reactContext) {
    super(reactContext);
  }

  @Override
  public String getName() {
    return "RNBaggageModule";
  }

  @ReactMethod
  public void launch(Callback errorCallback, Callback successCallback) {
      try {
          ReactApplicationContext context = getReactApplicationContext();
          Intent intent = new Intent(context, ARActivity.class);
          intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
          context.startActivity(intent);
          successCallback.invoke("Class: BridgeModule - launchNative");
      } catch (IllegalViewOperationException e) {
          errorCallback.invoke(e.getMessage());
      }
    }
}
