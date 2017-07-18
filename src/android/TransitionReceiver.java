import com.cowbell.cordova.geofence.Gson;
import com.cowbell.cordova.geofence.GeoNotification;

public class TransitionReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String error = intent.getStringExtra("error");

        if (error != null) {
            //handle error
            Log.println(Log.ERROR, "YourAppTAG", error);
        } else {
            String geofencesJson = intent.getStringExtra("transitionData");
            GeoNotification[] geoNotifications = Gson.get().fromJson(geofencesJson, GeoNotification[].class);
            //handle geoNotifications objects
        }
    }
}
