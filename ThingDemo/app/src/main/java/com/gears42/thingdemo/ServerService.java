package com.gears42.thingdemo;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.text.format.Formatter;
import android.util.Log;
import com.gears42.iot.webthing.ServerManager;
import com.gears42.iot.webthing.ServerSettings;
import com.gears42.iot.webthing.Thing;
import com.gears42.iot.webthing.Utils;
import java.util.HashMap;

public class ServerService extends Service {

    static HashMap<String, Thing> listOfThings = new HashMap<>();
    public static final String CHANNEL_ID = "com.gears42.thing.test";
    public static final int PORT = 8889;
    public static final String EMAIL = "say-hello@thing.com";
    public static final String PASSWORD = "42gears";
    public static final String KEY = "42GearsMobilitySystemsPrivateLimitedIndia042GearsMobilitySystemsPrivateLimitedIndia042GearsMobilitySystemsPrivateLimitedIndia";
    String DEVICE_IP = "";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        try {

            Intent notificationIntent = new Intent(this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this,
                    0, notificationIntent, 0);

            Notification notification = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                notification = new Notification.Builder(this, CHANNEL_ID)
                        .setContentTitle("Things Server")
                        .setContentText("Running")
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentIntent(pendingIntent)
                        .setOngoing(true)
                        .build();
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    notification = new Notification.Builder(this)
                            .setContentTitle("Things Server")
                            .setContentText("Running")
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentIntent(pendingIntent)
                            .setOngoing(true)
                            .build();
                }
            }

            //Check if server is already started
            if (!ServerManager.isServerRunnning()) {

                getIPAddresOfDevice();
                startServer();

            }

            startForeground(101, notification);
        } catch (Exception e) {
            Log.d("thing", e.getLocalizedMessage());
        }
        return START_STICKY;
    }

    //Get ip address of device
    public void getIPAddresOfDevice() {
        try {

            WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
            DEVICE_IP = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());

        } catch (Exception e) {
            Log.d("thing", e.getLocalizedMessage());
        }
    }

    //For creating thing.
    public void createThing() {

        //Create a thing and add it to listOfThings hashmap.
        BulbThing bulbThing=new BulbThing("BulbThing","Smart Bulb of Office","4242AB42422","Bulb1x");
        listOfThings.put(bulbThing.getDeviceId(),bulbThing);

    }

    //For starting server.
    public void startServer() {

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {

                    createThing();

                    //Make sure listOfThings contains at least one thing.
                    if (listOfThings.size() != 0 && !ServerManager.isServerRunnning()) {

                        ServerManager serverManager = new ServerManager();

                        //You can also change default credentials.
                        ServerSettings.setEmail(EMAIL);
                        ServerSettings.setPassword(PASSWORD);
                        ServerSettings.setKey(KEY);

                        /*
                         Server will start with the things in listOfThings, Where context is the application context.
                        'Smart Bulbs Connector' is the name of the connector,
                         Constants.PORT is the port on which server runs,
                         DEVICE_IP is the IP address of the android device,
                         true indicates to use https for the server,
                         1.0 is the version of the connector.
                        */

                        serverManager.startServer(MainActivity.context,listOfThings,new ServerSettings("Smart Bulbs Connector", PORT, Utils.getIP(),false, 1.0));
                    } else {
                        Log.d("thing", "No list of thing to host");
                    }

                } catch (Exception e) {
                    Log.d("thing", e.getLocalizedMessage());
                }
            }
        });
    }
}
