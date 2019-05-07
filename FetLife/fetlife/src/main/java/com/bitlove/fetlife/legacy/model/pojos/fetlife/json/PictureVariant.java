
package com.bitlove.fetlife.legacy.model.pojos.fetlife.json;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PictureVariant {

    @JsonProperty("url")
    private String url;

    @JsonProperty("url")
    public String getUrl() {
        return url;
    }

    @JsonProperty("url")
    public void setUrl(String url) {
        this.url = url;
    }

}
