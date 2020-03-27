package com.gears42.thingdemo;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbManager;
import android.net.Credentials;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.text.format.Formatter;
import android.util.Log;

import com.gears42.iot.webthing.CredentialsIx;
import com.gears42.iot.webthing.ServerManager;
import com.gears42.iot.webthing.Thing;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;

public class ServerService extends Service {

    boolean isServerStarted = false;
    public CredentialsIx credentialsIx;
    static HashMap<String, Thing> listOfThings = new HashMap<>();
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
            if (!isServerStarted) {

                getIPAddresOfDevice();
                initializeCredentials();
                startServer();

            }

            startForeground(101, notification);
        }catch (Exception e){
            Log.d("thing",e.getLocalizedMessage());
        }
        return START_STICKY;
    }

    public void getIPAddresOfDevice(){
        try{

            //get ip address of device and add it to a utility
            WifiManager wm = (WifiManager)getApplicationContext().getSystemService(WIFI_SERVICE);
            Utility.DEVICE_IP = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
            rootPath= Environment.getExternalStorageDirectory().toString();

        }catch(Exception e){
            Log.d("thing",e.getLocalizedMessage());
        }

    }

    public void initializeCredentials(){

            //credential class to initialize email and password which is used for login
            credentialsIx=new Credential();
            credentialsIx.setEmail(Utility.EMAIL);
            credentialsIx.setPassword(Utility.PASSWORD);
            credentialsIx.setKey(Utility.KEY);

    }

    public void createThing(){

        //create a list of properties with value
        Dictionary<String, String> properties=new Hashtable<String, String>();
        properties.put("bulb-switch","false");
        properties.put("bulb-brightness","50");

        //create a bulb thing
        BulbThing bulbThing=new BulbThing("SmartBulb", "Manage Smart Bulb","DEVICEID-12345", properties);

        //make sure device-uniqueid should be same in thing class constructor and in list of thing
        listOfThings.put("DEVICEID-12345",bulbThing);

    }

    public void startServer() {

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {

                    createThing();

                    //make sure list contains things
                    if(listOfThings.size()!=0 && !ServerManager.isServerRunnning())  {

                        //server will start with list of things at specified port, device-ip is used to access server from different devices, rootpath is path where server's file is created, broadcast receiver class is used for restarting server in android
                        ServerManager serverManager = new ServerManager();
                        serverManager.startServer(listOfThings, "List of Things", Utility.PORT, Utility.DEVICE_IP, credentialsIx, rootPath, false,"com.gears42.thingdemo.RestartAppIntentReceiver");
                        isServerStarted=true;
                    }else{
                        Log.d("thing","No list of thing to host");
                        isServerStarted=false;
                    }

                }catch(Exception e){
                    Log.d("thing",e.getLocalizedMessage());
                }
            }
        });

    }
}
