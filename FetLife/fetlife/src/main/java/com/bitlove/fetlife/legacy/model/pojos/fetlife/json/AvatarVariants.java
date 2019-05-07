package com.bitlove.fetlife.legacy.model.pojos.fetlife.json;

import com.bitlove.fetlife.legacy.model.pojos.fetlife.MediaVariantsInterface;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AvatarVariants implements MediaVariantsInterface {

    @JsonProperty("huge")
    private String huge;

    @JsonProperty("large")
    private String large;

    @JsonProperty("medium")
    private String medium;

    public String getHuge() {
        return huge;
    }

    public void setHuge(String huge) {
        this.huge = huge;
    }

    public String getLarge() {
        return large;
    }

    public void setLarge(String large) {
        this.large = large;
    }

    public String getMedium() {
        return medium;
    }

    public void setMedium(String medium) {
        this.medium = medium;
    }

    @Override
    public String getHugeUrl() {
        return huge != null ? huge : medium;
    }

    @Override
    public String getLargeUrl() {
        return large != null ? large : medium;
    }

    @Override
    public String getMediumUrl() {
        return medium;
    }
}
