package com.bitlove.fetlife.model.pojos;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "secondary_target",
        "target"
})
public class Event {

    @JsonProperty("secondary_target")
    private SecondaryTarget secondaryTarget;
    @JsonProperty("target")
    private Target target;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     *
     * @return
     * The secondaryTarget
     */
    @JsonProperty("secondary_target")
    public SecondaryTarget getSecondaryTarget() {
        return secondaryTarget;
    }

    /**
     *
     * @param secondaryTarget
     * The secondary_target
     */
    @JsonProperty("secondary_target")
    public void setSecondaryTarget(SecondaryTarget secondaryTarget) {
        this.secondaryTarget = secondaryTarget;
    }

    /**
     *
     * @return
     * The target
     */
    @JsonProperty("target")
    public Target getTarget() {
        return target;
    }

    /**
     *
     * @param target
     * The target
     */
    @JsonProperty("target")
    public void setTarget(Target target) {
        this.target = target;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }
}
