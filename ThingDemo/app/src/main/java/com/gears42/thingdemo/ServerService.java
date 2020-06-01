package com.gears42.thingdemo;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.text.format.Formatter;
import android.util.Log;

import com.gears42.iot.webthing.ServerManager;
import com.gears42.iot.webthing.Thing;


import java.util.HashMap;

public class ServerService extends Service {

    static HashMap<String, Thing> listOfThings = new HashMap<>();
    String DEVICE_IP = "";
    int PORT = 8889;
    String rootPath;


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
                notification = new Notification.Builder(this, MainActivity.CHANNEL_ID)
                        .setContentTitle("Things Server")
                        .setContentText("Running")
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentIntent(pendingIntent)
                        .setOngoing(true)
                        .build();
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    notification = new Notification.Builder(this)
                            .setContentTitle("Things Server ")
                            .setContentText("Running")
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentIntent(pendingIntent)
                            .setOngoing(true)
                            .build();
                }
            }

            //check if server is already started or not
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

    public void getIPAddresOfDevice() {
        try {

            //get ip address of device
            WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
            DEVICE_IP = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
            rootPath = Environment.getExternalStorageDirectory().toString();

        } catch (Exception e) {
            Log.d("thing", e.getLocalizedMessage());
        }

    }

    public void createThing() {

        //create a thing and add it to listOfThings hashmap.
        BulbThing bulbThing=new BulbThing("BulbThing","Smart Bulb of Office","4242AB42422");
        listOfThings.put("4242AB42422",bulbThing);

    }

    public void startServer() {

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {

                    createThing();

                    //make sure list contains things
                    if (listOfThings.size() != 0 && !ServerManager.isServerRunnning()) {

                        //server will start with list of things at specified port, device-ip is used to access server from different devices, rootpath is path where server's file is created, Whether https is true or false. false will start server on http, broadcast receiver class is used for restarting server in android
                        ServerManager serverManager = new ServerManager();

                        //if you want to change default credentials,
                        ServerManager.setEmail("say-hello@thing.com");
                        ServerManager.setPassword("42gears");

                        serverManager.startServer(listOfThings, "Smart Bulbs", PORT, DEVICE_IP, rootPath, false, "com.gears42.thingdemo.RestartAppIntentReceiver");
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
