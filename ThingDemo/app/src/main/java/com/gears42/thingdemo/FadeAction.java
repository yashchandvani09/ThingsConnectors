package com.gears42.thingdemo;

import com.gears42.iot.webthing.Action;
import com.gears42.iot.webthing.ActionResponse;
import com.gears42.iot.webthing.Thing;
import com.google.gson.JsonObject;

import java.util.UUID;

public class FadeAction extends Action {

    public FadeAction(Thing thing, JsonObject input) {
        super(UUID.randomUUID().toString(), thing, "fade", input);
    }

    @Override
    public ActionResponse performAction(ActionResponse actionResponse) {

        Thing thing = this.getThing();
        JsonObject input = this.getInput();

        //TODO
        //perform fade action

        //if action is failed then change status
        //actionResponse.setReason("reason for failure");
        //actionResponse.setStatus(ActionResponse.ACTION_STATUS.FAILED);


        actionResponse.setStatus(ActionResponse.ACTION_STATUS.COMPLETED);
        return actionResponse;
    }

}