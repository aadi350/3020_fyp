package com.hellobridge;

import com.facebook.react.ReactActivity;

public class MainActivity extends ReactActivity {

    public static String REQ_MSG = "ARActivity Launched";

    /**
   * Returns the name of the main component registered from JavaScript. This is used to schedule
   * rendering of the component.
   */
  @Override
  protected String getMainComponentName() {
    return "HelloBridge";
  }
}
