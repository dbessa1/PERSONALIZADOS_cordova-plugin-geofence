package com.cowbell.cordova.geofence;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.ArrayList;
import java.util.List;

import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;

public class ReceiveTransitionsIntentService extends IntentService {
    protected static final String GeofenceTransitionIntent = "com.cowbell.cordova.geofence.TRANSITION";
    protected BeepHelper beepHelper;
    protected GeoNotificationNotifier notifier;
    protected GeoNotificationStore store;

    /**
     * Sets an identifier for the service
     */
    public ReceiveTransitionsIntentService() {
        super("ReceiveTransitionsIntentService");
        beepHelper = new BeepHelper();
        store = new GeoNotificationStore(this);
        Logger.setLogger(new Logger(GeofencePlugin.TAG, this, false));
    }

    /**
     * Handles incoming intents
     *
     * @param intent
     *            The Intent sent by Location Services. This Intent is provided
     *            to Location Services (inside a PendingIntent) when you call
     *            addGeofences()
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        Logger logger = Logger.getLogger();
        logger.log(Log.DEBUG, "ReceiveTransitionsIntentService - onHandleIntent");
        Intent broadcastIntent = new Intent(GeofenceTransitionIntent);
        notifier = new GeoNotificationNotifier(
            (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE),
            this
        );

        // TODO: refactor this, too long
        // First check for errors
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            // Get the error code with a static method
            int errorCode = geofencingEvent.getErrorCode();
            String error = "Location Services error: " + Integer.toString(errorCode);
            // Log the error
            logger.log(Log.ERROR, error);
            broadcastIntent.putExtra("error", error);
        } else {
            // Get the type of transition (entry or exit)
            int transitionType = geofencingEvent.getGeofenceTransition();
            if (transitionType == Geofence.GEOFENCE_TRANSITION_ENTER)
                {
        PowerManager pm = (PowerManager)getSystemService(Context.POWER_SERVICE);// tentar (PowerManager) getSystemService(POWER_SERVICE);
        WakeLock wakeLock = pm.newWakeLock((PowerManager.PARTIAL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP), "TAG");
        wakeLock.acquire();//cancelei porque ja tenho o plugin de background(wake lock)//REATIVEI, PORQUE APESAR
        //DE JA TER UM PLUGIN DE WAKE LOCK, ESTE SCRIPT TODO RODA ANTES DA ABERTURA DO APP QUE ACINA O PLUGIN DE WAKE LOCK, E 
        //TALVES POR ISTO ESTE SCRIPT NECESSITE DE UMA SOLICITAÇÃO DE WAKE LOCK PARA ABRIR
        //O APP QUANDO O CELULAR ESTÁ HIBERNANDO A MUITO TEMPO POR EXEMPLO.
      
      String app_ja_aberto="nao";
      ActivityManager activityManager = (ActivityManager) this.getSystemService( ACTIVITY_SERVICE );
 List<RunningAppProcessInfo> procInfos = activityManager.getRunningAppProcesses();
 for(int i = 0; i < procInfos.size(); i++){
   if(procInfos.get(i).processName.equals("com.grantec.filhorapido"))
   {
    Log.e("Result", "App is running - Doesn't need to reload");
    app_ja_aberto="sim";
    break;
   }
}
    if(app_ja_aberto == "nao")
    {
                //------INICIO---------ABRE MEU APP--------------
             String  packageN = "com.grantec.filhorapido";//NOME DO MEU APP
            Intent i = getPackageManager().getLaunchIntentForPackage(packageN);
            if (i != null) {
                i.addCategory(Intent.CATEGORY_LAUNCHER);
                startActivity(i);
                //------FIM---------ABRE MEU APP--------------
                //this.moveTaskToBack(true); isto deveRIA minimizar a tela do app 
            }
      }
                    
                }
                
                
            if ((transitionType == Geofence.GEOFENCE_TRANSITION_ENTER)
                    || (transitionType == Geofence.GEOFENCE_TRANSITION_EXIT)) {
                logger.log(Log.DEBUG, "Geofence transition detected");
                List<Geofence> triggerList = geofencingEvent.getTriggeringGeofences();
                List<GeoNotification> geoNotifications = new ArrayList<GeoNotification>();
                for (Geofence fence : triggerList) {
                    String fenceId = fence.getRequestId();
                    GeoNotification geoNotification = store
                            .getGeoNotification(fenceId);

                    if (geoNotification != null) {
                        if (geoNotification.notification != null) {
                            notifier.notify(geoNotification.notification);//CANCELEI AQUI A NOTIFICACAO AO USUARIO
                        }
                        geoNotification.transitionType = transitionType;
                        geoNotifications.add(geoNotification);
                    }
                }

                if (geoNotifications.size() > 0) {
                    broadcastIntent.putExtra("transitionData", Gson.get().toJson(geoNotifications));
                    GeofencePlugin.onTransitionReceived(geoNotifications);
                }
            } else {
                String error = "Geofence transition error: " + transitionType;
                logger.log(Log.ERROR, error);
                broadcastIntent.putExtra("error", error);
            }
        }
        sendBroadcast(broadcastIntent);
        
    }
}
