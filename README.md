
# react-native-baggage-module

## Getting started

`$ npm install /BaggageModule --save`


### Manual installation


#### Android

1. Open up `App.js` file
2. Add `let ARModule = NativeModules.RNBaggageModule' after import section 
3. Implement React Native function launch() with two parameters:
    Callback errorCallback
    Callback successCallback
4. Call launch() via async function to launch the AR Native activity
