
package com.reactlibrary;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import android.widget.Toast;

public class RNBaggageModuleModule extends ReactContextBaseJavaModule {

  private final ReactApplicationContext reactContext;

  public RNBaggageModuleModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
  }

  @Override
  public String getName() {
    return "RNBaggageModule";
  }

  @ReactMethod
  public void launch() {
      System.out.println("Native launched");
      CharSequence text = "Hello toast!";
      int duration = Toast.LENGTH_LONG;

      Toast toast = Toast.makeText(reactContext, text, duration);
      toast.show();
    }
}
