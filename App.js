/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 * @flow
 */
 
import React, { Component } from 'react';
import {
  Button,
  Image,
  Platform,
  StyleSheet,
  Text,
  TouchableOpacity,
  View
} from 'react-native';
 
// We are importing the native Java module here
import {NativeModules} from 'react-native';
var Bridge = NativeModules.Bridge;
 
type Props = {};
export default class App extends Component<Props> {
 
  // async function to call the Java native method
  async launchExternal() {
    console.log("JS: launchExternal");
    Bridge.launchNative( (err) => {console.log(err)}, (msg) => {console.log(msg)});
  }

 
  render() {
    return (
      <View style={styles.container}>
         <Image
          style={{width: 240, height: 100, marginBottom:0}}
          source={require('./black_cal_logo.png')}
        />

        <Text style={styles.titleText}>
          AR Baggage Measure
        </Text>

        <Button title={"Launch AR"} onPress={ this.launchExternal } style={styles.buttonStyle}>
              <Text>Invoke native Java code</Text>
         </Button>
         
      </View>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#F5FCFF',
  },
  titleText: {
    justifyContent: 'center',
    alignItems: 'center',
    color: 'grey',
    marginBottom: 40,
    fontSize: 30,
    fontFamily: 'Roboto'

  },
  buttonStyle: {
    backgroundColor: '#FFFFFF'
  },
});
