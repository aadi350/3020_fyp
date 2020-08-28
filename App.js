/* eslint-disable react-native/no-inline-styles */
/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 * @flow strict-local
 */

import React, {Component} from 'react';
import {Image, View, NativeModules, Button } from 'react-native';
import {Card, CardItem, Text} from 'native-base';

let ARModule = NativeModules.RNBaggageModule;

export default class App extends Component {
  async launchModule() {
    console.log('[App.js] launchModule');
    ARModule.launch(
      msg => {
        console.log(msg);
      },
      suc => {
        console.log(suc);
      },
    );
  };



  render() {
    const viewStyle = {
      flex:3,
      flexDirection:'column',
      alignItems:'center',
      justifyContent:'center'
    }

    const buttonStyle = {
      color: '#BB29BB'
    }

    const logoStyle = {
      width: 275,
      height: 275,
      resizeMode: 'center',
      marginTop: 0,
      marginBottom: -25
    }

    return (
      <View style={viewStyle}>

          <Image
            source={require('./assets/images/app_icon_light.png')}
            style={logoStyle}
          />
          <CardItem bordered style={{justifyContent: 'center'}}>
            <Button
              color= "#BB29BB"
              style={buttonStyle}
              title={'Launch AR'}
              onPress={this.launchModule}>
              <Text>Launch AR</Text>
            </Button>
          </CardItem>

      </View>
    );
  }
}
