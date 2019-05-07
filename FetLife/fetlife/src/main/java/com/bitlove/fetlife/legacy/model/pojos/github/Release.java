package com.bitlove.fetlife.legacy.model.pojos.github;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Release {

    @JsonProperty("name")
    private String name;

    @JsonProperty("tag_name")
    private String tag;

    @JsonProperty("assets")
    private List<Asset> assets;

    @JsonProperty("prerelease")
    private boolean prerelease;

    public String getName() {
        return name;
    }

    public List<Asset> getAssets() {
        return assets;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAssets(List<Asset> assets) {
        this.assets = assets;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public boolean isPrerelease() {
        return prerelease;
    }

    public void setPrerelease(boolean prerelease) {
        this.prerelease = prerelease;
    }

    public String getReleaseUrl() {
        if (assets == null || assets.isEmpty()) {
            return null;
        }
        return assets.get(0).getBrowserDownloadUrl();
    }
}
