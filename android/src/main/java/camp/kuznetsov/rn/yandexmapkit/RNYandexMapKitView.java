package camp.kuznetsov.rn.yandexmapkit;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PointF;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.events.RCTEventEmitter;
import com.yandex.mapkit.Animation;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.layers.ObjectEvent;
import com.yandex.mapkit.location.Location;
import com.yandex.mapkit.location.LocationListener;
import com.yandex.mapkit.location.LocationStatus;
import com.yandex.mapkit.map.CameraListener;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.CameraUpdateSource;
import com.yandex.mapkit.map.Map;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.mapkit.user_location.UserLocationLayer;
import com.yandex.mapkit.user_location.UserLocationObjectListener;
import com.yandex.mapkit.user_location.UserLocationView;

import java.math.BigDecimal;
import java.math.RoundingMode;

import javax.annotation.Nullable;

public class RNYandexMapKitView extends MapView implements CameraListener {

    public static final String MAP_EVENT = "onMapEvent";

    public RNYandexMapKitView(Context context) {
        super(context);
        this.onStart();
        this.getMap().addCameraListener(this);
        this.getMap().move(new CameraPosition(new Point(0, 0), 14, 0, 0));
    }

    public RNYandexMapKitView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override
    public void onCameraPositionChanged(Map map, CameraPosition cameraPosition, CameraUpdateSource cameraUpdateSource, boolean finished) {
        if(finished) {
            Log.d("location", "location changed to " + cameraPosition.getTarget().toString());
            Point mapCenter = cameraPosition.getTarget();
            WritableMap payload = Arguments.createMap();
            double mapLatitude = BigDecimal.valueOf(mapCenter.getLatitude())
                    .setScale(6, RoundingMode.HALF_UP)
                    .doubleValue();
            double mapLongitude = BigDecimal.valueOf(mapCenter.getLongitude())
                    .setScale(6, RoundingMode.HALF_UP)
                    .doubleValue();
            payload.putDouble("latitude",  mapLatitude);
            payload.putDouble("longitude", mapLongitude);
            ReactContext reactContext = (ReactContext) getContext();
            reactContext.getJSModule(RCTEventEmitter.class).receiveEvent(this.getId(), MAP_EVENT, payload);
        }
    }

    public void animateToCoordinate(@Nullable Point coordinate){
        if (coordinate == null){
            MapKitFactory.getInstance().createLocationManager().requestSingleUpdate(new LocationListener() {
                @Override
                public void onLocationUpdated(Location location) {
                    CameraPosition cameraPosition = getMap().getCameraPosition();
                    Point currentPoint = cameraPosition.getTarget();
                    Point myLocation = location.getPosition();
                    if (myLocation != null
                            && myLocation.getLatitude() != currentPoint.getLatitude()
                            && myLocation.getLongitude() != currentPoint.getLongitude()){
                        getMap().move(
                                new CameraPosition(myLocation,
                                        cameraPosition.getZoom(),
                                        cameraPosition.getAzimuth(),
                                        cameraPosition.getTilt()),
                                new Animation(Animation.Type.SMOOTH, 0),
                                null
                        );
                    }
                }

                @Override
                public void onLocationStatusUpdated(LocationStatus locationStatus) {

                }
            });
        }
        else {
            CameraPosition cameraPosition = this.getMap().getCameraPosition();
            Point point = cameraPosition.getTarget();
            if (point.getLatitude() != coordinate.getLatitude() && point.getLongitude() != coordinate.getLongitude()) {
                this.getMap().move(
                        new CameraPosition(coordinate,
                                cameraPosition.getZoom(),
                                cameraPosition.getAzimuth(),
                                cameraPosition.getTilt()),
                        new Animation(Animation.Type.SMOOTH, 0),
                        null
                );
            }

        }
    }
}