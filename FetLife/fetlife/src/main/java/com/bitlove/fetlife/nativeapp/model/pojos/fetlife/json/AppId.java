package com.bitlove.fetlife.nativeapp.model.pojos.fetlife.json;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AppId {

    @JsonProperty("id")
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
