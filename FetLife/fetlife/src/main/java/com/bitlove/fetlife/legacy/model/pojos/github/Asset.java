package com.bitlove.fetlife.legacy.model.pojos.github;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Asset {

    @JsonProperty("name")
    private String name;

    @JsonProperty("browser_download_url")
    private String browserDownloadUrl;

    public String getName() {
        return name;
    }

    public String getBrowserDownloadUrl() {
        return browserDownloadUrl;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBrowserDownloadUrl(String browserDownloadUrl) {
        this.browserDownloadUrl = browserDownloadUrl;
    }
}
