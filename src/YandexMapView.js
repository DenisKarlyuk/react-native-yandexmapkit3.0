import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { 
  requireNativeComponent, 
  findNodeHandle, 
  NativeModules, 
  View, 
  StyleSheet, 
  Platform,
  Image} from 'react-native';
import {makeDebouncedGeocoding} from './YandexMapKit';

class YandexMapView extends Component {

  _prevLocation = null;
  _map = null;
  _debouncedGeocoding = null;

  componentDidMount() {
    const { location} = this.props;
    if (location) {
      this._map.setNativeProps({ location });
    }
    const {geocodingOptions, geocodingApiKey, onGeocoding} = this.props;
    this._debouncedGeocoding = makeDebouncedGeocoding(geocodingOptions, onGeocoding, geocodingApiKey);
  }

  componentWillReceiveProps(nextProps) {
    if (
      nextProps.geocodingApiKey != this.props.geocodingApiKey || 
      nextProps.geocodingOptions != this.props.geocodingOptions ||
      nextProps.onGeocoding != this.props.onGeocoding
    ){
      this._debouncedGeocoding = makeDebouncedGeocoding(nextProps.geocodingOptions, nextProps.onGeocoding, nextProps.geocodingApiKey);
    }
  }

  componentWillUpdate(nextProps) {
    if (!this._prevLocation || !nextProps.location)
      return;
    if (
      this._prevLocation.latitude  !== nextProps.location.latitude ||
      this._prevLocation.longitude  !== nextProps.location.longitude
    ) {
      this._map.setNativeProps({ location: nextProps.location });
    }
  }

  componentWillUnmount() {
    if (this._debouncedGeocoding){
      this._debouncedGeocoding.cancel();
    }
  }

  render() {
    //Omit region and set it via setNativeProps
    const {style, showMyLocation, ...rest} = this.props;
    return (
        <RNYandexMapView ref={map => {this._map = map}} 
                        {...rest} 
                        style={styles.container}
                        onMapEvent={this.onMapEventInternal}
                        showMyLocation={showMyLocation}
                        onGeocodingEvent={this.onGeocodingEventInternal}
                        />
    );
  }

  onMapEventInternal = (event) => {
    const {latitude, longitude} = event.nativeEvent;
    this._prevLocation = {latitude, longitude};
    if (this.props.onInteraction){
      this.props.onInteraction(event.nativeEvent);
    }
    
    //Handle geocoding
    const {geocodingEnabled} = this.props;
    if (geocodingEnabled)
    {
      this._debouncedGeocoding(latitude, longitude);
    }
  };

  //Native android-only event
  onGeocodingEventInternal = (event) => {
    if (this.props.onGeocoding){
      this.props.onGeocoding(event.nativeEvent);
    }
  };

  runCommand = (name, args) => {
    switch (Platform.OS) {
      case 'android':
        NativeModules.UIManager.dispatchViewManagerCommand(
          findNodeHandle(this._map),
          NativeModules.UIManager.RNYandexMapView.Commands[name],
          args
        );
        break;

      case 'ios':
        NativeModules.RNYandexMapViewManager[name].apply(
          NativeModules.RNYandexMapViewManager[name],
          [findNodeHandle(this._map), ...args]
        );
        break;

      default:
        break;
    }
  };

  /**
   * Animates to given {latitude,longitude}, or to user's location, if coordinate is undefined
   */
  animateToCoordinate = (coordinate) => {
    this.runCommand('animateToCoordinate', [coordinate]);
  };
}

YandexMapView.defaultProps = {
    geocodingOptions: {
        sco: 'latlong',
        kind: 'house',
    },
};

YandexMapView.propTypes = {
    showMyLocation: PropTypes.bool,

    //Geocoding
    geocodingEnabled: PropTypes.bool,
    geocodingOptions: PropTypes.object,
    geocodingApiKey: PropTypes.string,

    location: PropTypes.shape({
        latitude: PropTypes.number,
        longitude: PropTypes.number,
    }),
    onInteraction: PropTypes.func,
    onGeocoding: PropTypes.func,
    ...View.propTypes
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
  }
});

const RNYandexMapView = requireNativeComponent('RNYandexMapView', YandexMapView, {nativeOnly: {onMapEvent: true, onGeocodingEvent: true}});

module.exports = YandexMapView;
