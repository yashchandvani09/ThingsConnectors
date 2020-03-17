package com.gears42.thingdemo;

import android.util.Log;
import android.widget.Toast;

import com.gears42.iot.webthing.Action;
import com.gears42.iot.webthing.Thing;
import com.google.gson.JsonObject;



import java.util.UUID;

public class BulbStateAction extends Action {

    public BulbStateAction(Thing thing, JsonObject input) {
        super(UUID.randomUUID().toString(), thing, "fade", input);
    }

    @Override
    public void performAction() {
        Thing thing = this.getThing();
        JsonObject input = this.getInput();

        //here we can access different properties of action class
        Log.d("thing","Input="+this.getInput().toString()+" Name="+this.getName());

        Toast.makeText(MyApplication.getContext(),"Some Properties are changed from SureMDM",Toast.LENGTH_LONG).show();
    }

}
