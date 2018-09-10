package camp.kuznetsov.rn.yandexmapkit;

import android.util.Log;

import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.yandex.mapkit.Animation;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.BoundingBox;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.map.CameraListener;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.mapview.MapView;

import java.util.Map;

import javax.annotation.Nullable;

public class RNYandexMapKitManager extends SimpleViewManager<RNYandexMapKitView> {
    public static final String REACT_CLASS = "RNYandexMapView";

    private static final int ANIMATE_TO_COORDINATE = 1;

    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @Override
    protected RNYandexMapKitView createViewInstance(ThemedReactContext reactContext) {
        RNYandexMapKitModule nativeModule = reactContext.getNativeModule(RNYandexMapKitModule.class);
        String apiKey = nativeModule.getApiKey();
        MapKitFactory.setApiKey(apiKey);
        MapKitFactory.initialize(reactContext);
        MapKitFactory.getInstance().onStart();
        RNYandexMapKitView view = new RNYandexMapKitView(reactContext);
        view.onStart();
        return view;
    }

    @ReactProp(name="location")
    public void setLocation(RNYandexMapKitView mapView, ReadableMap location){
        double latitude = location.getDouble("latitude");
        double longitude = location.getDouble("longitude");
        CameraPosition current = mapView.getMap().getCameraPosition();
        mapView.getMap().move(new CameraPosition(new Point(latitude, longitude), current.getZoom(), current.getAzimuth(), current.getTilt()));
    }

    @Nullable
    @Override
    public Map<String, Object> getExportedCustomDirectEventTypeConstants() {
        MapBuilder.Builder<String, Object> builder = MapBuilder.builder();
        builder.put(RNYandexMapKitView.MAP_EVENT,       MapBuilder.of("registrationName", RNYandexMapKitView.MAP_EVENT));
        return builder.build();
    }


    @Override
    @Nullable
    public Map<String, Integer> getCommandsMap() {
        return MapBuilder.of(
            "animateToCoordinate", ANIMATE_TO_COORDINATE
        );
    }

    @Override
    public void receiveCommand(RNYandexMapKitView view, int commandId, @Nullable ReadableArray args) {
        switch (commandId) {
            case ANIMATE_TO_COORDINATE:
                Point coordinate = null;
                ReadableMap latlon = args.getMap(0);
                if (latlon != null){
                    double latitude = latlon.getDouble("latitude");
                    double longitude = latlon.getDouble("longitude");
                    coordinate = new Point(latitude, longitude);
                }
                view.animateToCoordinate(coordinate);
                break;
        }
    }

}
