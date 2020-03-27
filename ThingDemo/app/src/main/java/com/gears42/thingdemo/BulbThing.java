package com.gears42.thingdemo;

import com.gears42.iot.webthing.Property;
import com.gears42.iot.webthing.Thing;
import com.gears42.iot.webthing.Value;

import java.util.Arrays;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class BulbThing extends Thing {

    Value<Integer> valueBrightness;
    Value<Boolean> valueSwitch;

    public BulbThing(String name, String description, String uniqueID, Dictionary<String, String> properties) {
        super(name, Arrays.asList(name), description, uniqueID);

        //iterate list of properties and add properties dynamically which are available in list
        for (Enumeration<String> i = properties.keys(); i.hasMoreElements(); ) {

            String str = i.nextElement().toString();

            switch (str) {

                case "bulb-switch": {
                    Map<String, Object> mapSwitch = new HashMap<String, Object>();
                    mapSwitch.put("@type", "OnOffProperty");
                    mapSwitch.put("label", "On/Off");
                    mapSwitch.put("type", "boolean");
                    mapSwitch.put("readOnly", false);
                    mapSwitch.put("description", "Whether the lamp is turned on");
                    valueSwitch = new Value<Boolean>(Boolean.parseBoolean(properties.get(str)),isBulbOn->bulbUpdateFromSureMDMConsole(isBulbOn));
                    this.addProperty(new Property(this, "bulb-switch", valueSwitch, mapSwitch));
                    break;
                }

                case "bulb-brightness": {
                    Map<String, Object> mapBrightness = new HashMap<String, Object>();
                    mapBrightness.put("@type", "BrightnessProperty");
                    mapBrightness.put("label", "Brightness");
                    mapBrightness.put("type", "integer");
                    mapBrightness.put("readOnly", false);
                    mapBrightness.put("description","The level of light from 0-100");
                    mapBrightness.put("minimum", 0);
                    mapBrightness.put("maximum", 100);
                    valueBrightness = new Value<Integer>(Integer.parseInt(properties.get(str)),brightnessLevel->brightnessUpdateFromSureMDMConsole(brightnessLevel));
                    this.addProperty(new Property(this, "bulb-brightness", valueBrightness, mapBrightness));
                    break;
                }


            }

        }

        this.setHrefPrefix("/"+uniqueID);

        //adding action
        Map<String, Object> fadeActionMetadata = new HashMap<String, Object>();
        Map<String, Object> fadeActionInput = new HashMap<String, Object>();
        Map<String, Object> fadeActionProperties = new HashMap<String, Object>();
        Map<String, Object> fadeActionStatus = new HashMap<String, Object>();

        fadeActionStatus.put("type", "number");
        fadeActionProperties.put("fade", fadeActionStatus);

        fadeActionInput.put("type", "object");
        fadeActionInput.put("required", Arrays.asList(new String[]{"fade"}));
        fadeActionInput.put("properties", fadeActionProperties);

        fadeActionMetadata.put("label", "Fade");
        fadeActionMetadata.put("description", "Fade the lamp to a given level");
        fadeActionMetadata.put("input", fadeActionInput);

        this.addAvailableAction("fade", fadeActionMetadata,BulbStateAction.class);


        //sample input for this action
            /*
             	{"fade":{
				"input":{
					"fade":26}}
				}
             */

    }

    public void brightnessUpdateFromSureMDMConsole(int brightnessLevel){
        //apply this brightness level to your bulb
    }

    public void bulbUpdateFromSureMDMConsole(Boolean isBulbOn){
        //turn on/off your bulb
    }

    @Override
    public void update() {
        //this method is called when you retrieve properties to keep values up to date

        //if brightness value is changed from 50 to 80 from bulb itself
        valueBrightness.notifyOfExternalUpdate(80);
    }
}
