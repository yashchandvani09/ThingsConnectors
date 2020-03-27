package com.gears42.thingdemo;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.gears42.iot.webthing.Action;
import com.gears42.iot.webthing.ActionResponse;
import com.gears42.iot.webthing.Thing;
import com.google.gson.JsonObject;

import java.util.UUID;

public class BulbStateAction extends Action {

    public BulbStateAction(Thing thing, JsonObject input) {
        super(UUID.randomUUID().toString(), thing, "fade", input);
    }

    @Override
    public ActionResponse performAction(ActionResponse actionResponse) {
        Thing thing = this.getThing();
        JsonObject input = this.getInput();

        //here we can access different properties of action class
        Log.d("thing","Input="+this.getInput().toString()+" Name="+this.getName());

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MyApplication.getContext(),"Some Properties are changed from SureMDM",Toast.LENGTH_LONG).show();
            }
        });

        //if action is failed then change status
        //actionResponse.setReason("Your reason");
        //actionResponse.setStatus("Failed");

        actionResponse.setReason("completed");
        return actionResponse;
    }

}
