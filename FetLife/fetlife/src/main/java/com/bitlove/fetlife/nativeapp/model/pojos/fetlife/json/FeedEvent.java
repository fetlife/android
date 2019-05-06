
package com.bitlove.fetlife.nativeapp.model.pojos.fetlife.json;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FeedEvent {

    @JsonProperty("secondary_target")
    private SecondaryTarget secondaryTarget;

    @JsonProperty("target")
    private Target target;

    @JsonProperty("secondary_target")
    public SecondaryTarget getSecondaryTarget() {
        return secondaryTarget;
    }

    @JsonProperty("secondary_target")
    public void setSecondaryTarget(SecondaryTarget secondaryTarget) {
        this.secondaryTarget = secondaryTarget;
    }

    @JsonProperty("target")
    public Target getTarget() {
        return target;
    }

    @JsonProperty("target")
    public void setTarget(Target target) {
        this.target = target;
    }

}
