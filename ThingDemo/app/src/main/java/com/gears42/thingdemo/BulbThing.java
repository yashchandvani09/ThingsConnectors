package com.gears42.thingdemo;

import com.gears42.iot.webthing.Property;
import com.gears42.iot.webthing.Thing;
import com.gears42.iot.webthing.Value;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class BulbThing extends Thing {

    Value<Integer> valueBrightness;
    Value<Boolean> valueSwitch;
    Value<Integer> valueTemperature;

    public BulbThing(String name, String description, String uniqueID) {
        super(name, Arrays.asList(name), description, uniqueID);

        //add properties and actions
        Map<String, Object> mapSwitch = new HashMap<String, Object>();
        mapSwitch.put("@type", "OnOffProperty");
        mapSwitch.put("label", "On/Off");
        mapSwitch.put("type", "boolean");
        mapSwitch.put("readOnly", false);
        mapSwitch.put("description", "Whether the lamp is turned on");
        valueSwitch = new Value<Boolean>(true, isBulbOn -> bulbUpdateFromSureMDMConsole(isBulbOn));
        this.addProperty(new Property(this, "bulb-switch", valueSwitch, mapSwitch));

        Map<String, Object> mapBrightness = new HashMap<String, Object>();
        mapBrightness.put("@type", "BrightnessProperty");
        mapBrightness.put("label", "Brightness");
        mapBrightness.put("type", "number");
        mapBrightness.put("readOnly", false);
        mapBrightness.put("description", "The level of light from 0-100");
        mapBrightness.put("minimum", 50);
        mapBrightness.put("maximum", 150);
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

        //replace with your action class name, we will create action class in next step
        this.addAvailableAction("fade", fadeActionMetadata, FadeAction.class);

        //sample input for this action
        // {"fade":{ "input":{"fade":26,"duration":3}}}
    }

    //These methods will be called when property is changed from SureMDM console
    public void brightnessUpdateFromSureMDMConsole(int brightnessLevel) {
        //apply this brightness level to your bulb
    }

    public void bulbUpdateFromSureMDMConsole(Boolean isBulbOn) {
        //turn on/off your bulb
    }

    @Override
    public void update() {

        //this method is called when you retrieve properties to keep values up to date
        Random r = new Random();
        int Temprature = r.nextInt(20 - 40) + 20;
        valueTemperature.notifyOfExternalUpdate(Temprature);

    }

}