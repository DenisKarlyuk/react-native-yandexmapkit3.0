
# react-native-yandex-map-kit

Origin repo https://github.com/doomsower/react-native-yandexmapkit

Since original projects are in deep coma, here I only support features that I needed for my other projects. If you need markers, callouts or polygons, I suggest you use [react-native-maps](https://github.com/lelandrichardson/react-native-maps).
However, if you must use yandex map kit in your react-native projects and you need any of these features, I encourage you to contribute. Just be sure to check out both of the original SDKs before you implement anything, because they are quite different!

Note that Android version might not work properly on emulators, which results in map tiles not being rendered. 

![Android](https://raw.githubusercontent.com/doomsower/react-native-yandexmapkit/master/images/rn-ymk-android.gif)
![iOS](https://raw.githubusercontent.com/doomsower/react-native-yandexmapkit/master/images/rn-ymk-ios.gif)

## Installation

`$ npm install react-native-yandexmapkit --save`

### Android

1. Run `react-native link`
2. Add following permissions to your `android/app/src/AndroidManifest.xml`:
    
    ```xml
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
    <uses-permission android:name="android.permission.CHANGE_WIFI_STATE" />
    <uses-permission android:name="android.permission.READ_PHONE_STATE" />
    <uses-permission android:name="android.permission.WRITE_SETTINGS" />
    ```
3. Add yandexmapkit-android repo to your `android/build.gradle`:

    ```gradle
    allprojects {
      repositories {
          ...
          maven { url 'https://github.com/yandexmobile/yandexmapkit-android/raw/maven/' }
      }
    }
    ```

### iOS


1. This module requires CocoaPods to be used in iOS project. To add CocoaPods to your React Native project, follow steps 2 throught 7 of [this](https://blog.callstack.io/login-users-with-facebook-in-react-native-4b230b847899#.lai35aq3a) tutorial.
Add this line  

    ```ruby
    pod 'react-native-yandexmapkit', :path => '../node_modules/react-native-yandexmapkit'
    ```
    to your Podfile (you may need to adjust path if you have non-standard project structure).
		And then run `pod install` (if you’re setting up Cocoapods for the first time) or `pod update` (if you’re adding MoPub to an existing CocoaPods project).

2. Add `NSLocationWhenInUseUsageDescription` key in `Info.plist` if you want to display user's location.
3. Make sure that yandex maps can download stuff by configuring [App Transport Security](http://facebook.github.io/react-native/releases/0.33/docs/running-on-device-ios.html#app-transport-security)

## Usage

1. You'll need Yandex Map Kit API key. To obtain it, you need to send e-mail to Yandex support. You can find more info on this here: [EN](https://yandex.ru/legal/mapkit/?ncrnd=2604), [RU](https://yandex.ru/legal/mapkit/).

2. `import { YandexMapKit, YandexMapView } from 'react-native-yandexmapkit';`

3. Make sure to call `YandexMapKit.setApiKey(YANDEXMAPKIT_API_KEY);` before mounting `YandexMapView` component.

4. Render `YandexMapView` component:

    ```jsx
		<YandexMapView ref="yandexMap" onInteraction={this.onInteraction} region={this.state.region}
                           showMyLocation={true} geocodingEnabled={true} onGeocoding={this.onGeocoding}
                           showMyLocationButton={true}/>

    ```

### Component API

#### Props

| Prop | Type | Default | Description |
|---|---|---|---|
| `region` | `Object` |  | The region to be displayed by the map. <br/><br/>The region is defined by the center coordinates and the span of coordinates to display: `{latitude, longitude, latitudeDelta, longitudeDelta}`
| `initialRegion` | `Object` |  | The initial region to be displayed by the map.  Use this prop instead of `region` only if you don't want to control the viewport of the map besides the initial region.<br/><br/> Changing this prop after the component has mounted will not result in a region change.<br/><br/> This is similar to the `initialValue` prop of a text input.
| `showMyLocation` | `Boolean` | `false` | If `true` the map will show current user location marker. 
| `nightMode` | `Boolean` | `false` | A Boolean indicating whether the map should be rendered in a night mode.
| `showsTraffic` | `Boolean` | `false` | A Boolean value indicating whether the map displays traffic information.
| `geocodingEnabled` | `Boolean` | `false` | A Boolean value indicating whether the should send reverse [geocoding](https://tech.yandex.ru/maps/doc/geocoder/desc/concepts/About-docpage/) reqquests when region changes. These requests will return the description of geo object found in the center of the map.
| `disableAndroidGeocoding` | `Boolean` | `false` | If `true`, all reverse geocoding requests will be sent from JS. Otherwise, Android will use its own implementation.
| `geocodingApiKey` | `String` | | Yandex Maps API key to be used in geocoding requests, can be obtained [here](https://developer.tech.yandex.ru/). This key is different from Yande Map KIt API key.
| `geocodingOptions` | `Object` | {<br/>sco: 'latlong',<br/>kind: 'house'<br/>} | Reverse geocoding request [parameters](https://tech.yandex.ru/maps/doc/geocoder/desc/concepts/input_params-docpage/)
| `showMyLocationButton` | `Boolean` | `false` | If `true`, renders show my location button in top-right corner of the map
| `myLocationButtonPosition` | `Style` | { position: 'absolute', top: 16, right: 16, } | Style used to position container view with 'show my location' button inside it
| `renderMyLocationButton` | `Function` | | Use this to render custom 'show my location' button

#### Android-only props

The component exposes some Android-only props that control map UI:
- **showBuiltInScreenButtons**: PropTypes.bool,
- **showFindMeButton**: PropTypes.bool,
- **showJamsButton**: PropTypes.bool,
- **showScaleView**: PropTypes.bool,
- **showZoomButtons**: PropTypes.bool,
- **interactive**: PropTypes.bool,
- **hdMode**: PropTypes.bool,

#### Events

| Event Name | Returns | Notes
|---|---|---|
| `onInteraction` | `{latitude, longitude, latitudeDelta, longitudeDelta, type}` | Fired when user interacts with map.
|   |   | On **Android**, interaction type is returned in type attribute, list of types can be found [here](https://cdn.rawgit.com/yandexmobile/yandexmapkit-android/master/yandexmapkit-library/doc/ru/yandex/yandexmapkit/map/MapEvent.html)
|   |   | On **iOS**, this event is fired only at the end of interaction.
| `onGeocoding` | Objects | Takes two arguments: the first one is in native Android format, the second one is full response from web service.

#### Methods

| Method Name | Arguments | Notes
|---|---|---|
| `animateToCoordinate` | `PropTypes.shape({latitude: PropTypes.number, longitude: PropTypes.number})` | Animates map to given coordinate, or to user's current position if argument is undefined.

### YandexMapKit API

| Method Name | Arguments | Notes
|---|---|---|
| `setApiKey` | String | Sets Yandex Map Kit API key for all map views you use. Call this before you mount your first map view. 
| `requestGeocoding` | geocode: string, **Required**<br/>options: object,<br/>apikey: string | Helper method to call [Yandex Geocoder](https://tech.yandex.ru/maps/doc/geocoder/desc/concepts/input_params-docpage/), takes three arguments:<br/> **geocode** - queried address string or coordinate,<br/>**options** -well, options<br/>**apikey** - Yandex Maps API key for geocoding requests, this is different from Yandex Map Kit key<br/>Returns Promise which resolves with json 
| `makeDebouncedGeocoding` | options: object <br/> onComplete: function<br/> apiKey: string <br/> debounceWait: number | Convenience method to make Yandex Geocoder requests.<br/>**options** - geocoder request options<br/>**onComplete** - will be called with geocoder two arguments - first match in Android format, and full response<br/>**apiKey** - same as for *requestGeocoding*<br/>**debounceWait** - debouncing interval, defaults to 150 ms<br/><br/>Depending on **options** returns function that takes one argument - address, or two arguments - latitude and longitude (for reverse geocoding).
