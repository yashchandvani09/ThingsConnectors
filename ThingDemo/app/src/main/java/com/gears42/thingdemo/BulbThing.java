package com.gears42.thingdemo;

import android.util.Log;

import com.gears42.iot.webthing.Constants;
import com.gears42.iot.webthing.Property;
import com.gears42.iot.webthing.Thing;
import com.gears42.iot.webthing.Value;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class BulbThing extends Thing {

    Value<Integer> valueBrightness;
    Value<Boolean> valueSwitch;
    Value<Integer> valueTemperature;

    public BulbThing(String name, String description, String uniqueID,String model) {
        super(name, Arrays.asList(name), description, uniqueID,model);

        //add properties
        Map<String, Object> mapSwitch = new HashMap<String, Object>();
        mapSwitch.put("@type", "OnOffProperty");
        mapSwitch.put("label", "On/Off");
        mapSwitch.put("type", "boolean");
        mapSwitch.put("readOnly", false);
        mapSwitch.put("description", "Whether the lamp is turned on");
        /*
        EVENT polling type is for those properties, which we can know when its value is changed. Through a Broadcast message, etc.
        NORMAL polling type is for those properties, which we cannot know when its value is changed.
        NORMAL is use as default if no polling type is mentioned.
        */
        mapSwitch.put("POLLING_TYPE", Property.POLLING_TYPE.NORMAL);
        valueSwitch = new Value<Boolean>(true, isBulbOn -> bulbUpdateFromSureMDMConsole(isBulbOn));
        this.addProperty(new Property(this, "bulb-switch", valueSwitch, mapSwitch));

        Map<String, Object> mapBrightness = new HashMap<String, Object>();
        mapBrightness.put("@type", "BrightnessProperty");
        mapBrightness.put("label", "Brightness");
        mapBrightness.put("type", "number");
        mapBrightness.put("readOnly", false);
        mapBrightness.put("description", "The level of light from 0-100");
        mapBrightness.put("minimum", 0);
        mapBrightness.put("maximum", 100);
        valueBrightness = new Value<Integer>(50, brightnessLevel -> brightnessUpdateFromSureMDMConsole(brightnessLevel));
        this.addProperty(new Property(this, "bulb-brightness", valueBrightness, mapBrightness));

        Map<String, Object> mapTemperature = new HashMap<String, Object>();
        mapTemperature.put("@type", "TemperatureProperty");
        mapTemperature.put("label", "Temperature");
        mapTemperature.put("type", "number");
        mapTemperature.put("readOnly", true);
        mapTemperature.put("description", "Temperature of Smart Bulb");
        valueTemperature = new Value<Integer>(27);
        this.addProperty(new Property(this, "bulb-temperature", valueTemperature, mapTemperature));

        //The properties display order on the SureMDM console will be based on the creation order of the properties.

        //add action
        Map<String, Object> fadeActionMetadata = new HashMap<String, Object>();
        Map<String, Object> fadeActionInput = new HashMap<String, Object>();
        Map<String, Object> fadeActionProperties = new HashMap<String, Object>();
        Map<String, Object> fadeActionStatus = new HashMap<String, Object>();
        Map<String, Object> fadeActionDuration = new HashMap<String, Object>();

        fadeActionStatus.put("type", "number");
        fadeActionProperties.put("fade", fadeActionStatus);

        fadeActionDuration.put("type", "number");
        fadeActionProperties.put("duration", fadeActionDuration);

        fadeActionInput.put("type", "object");
        fadeActionInput.put("required", Arrays.asList(new String[]{"fade", "duration"}));
        fadeActionInput.put("properties", fadeActionProperties);

        fadeActionMetadata.put("label", "Fade");
        fadeActionMetadata.put("description", "Fade the lamp to a given level");
        fadeActionMetadata.put("input", fadeActionInput);

        //FadeAction.class performs the fade action.
        this.addAvailableAction("fade", fadeActionMetadata, FadeAction.class);

        //sample input for this action.
        // {"fade":{ "input":{"fade":26,"duration":3}}}


    }

    //Below two methods will be called when appropriate property is changed from SureMDM console.

    public void brightnessUpdateFromSureMDMConsole(int brightnessLevel) {
        //TODO
        //set the brightness level of bulb to 'brightnessLevel'.
    }

    public void bulbUpdateFromSureMDMConsole(Boolean isBulbOn) {
        //TODO
        //turn bulb on/off.
    }

    //this method is called to fetch latest values.
    @Override
    public void update(List<String> list) {
        //list contains names of properties to be updated.

        for(String propertyName : list){
            switch (propertyName){

                case "bulb-temperature":
                    Random random = new Random();
                    int temperature = random.nextInt(40-20) + 20;
                    valueTemperature.notifyOfExternalUpdate(temperature);
                    break;
                case "bulb-switch":
                    //TODO
                    //update latest value of bulb-switch
                    break;
                case "bulb-brightness":
                    //TODO
                    //update latest value of bulb-brightness
                    break;
                default:
                    //invalid property name
                    break;
            }
        }
    }

    @Override
    public boolean isThingOnline() {
        //this method tells if the Thing device is connected with the Android device.

       return true;
    }
}