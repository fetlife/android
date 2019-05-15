package com.bitlove.fetlife.github.dto;

import com.bitlove.fetlife.common.logic.databinding.BindableRecyclerAdapter;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Release implements BindableRecyclerAdapter.Diffable {

    @JsonProperty("id")
    private String id;

    @JsonProperty("body")
    private String body;

    @JsonProperty("name")
    private String name;

    @JsonProperty("tag_name")
    private String tag;

    @JsonProperty("assets")
    private List<Asset> assets;

    @JsonProperty("prerelease")
    private boolean prerelease;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

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

    @Override
    public boolean isSame(@NotNull BindableRecyclerAdapter.Diffable other) {
        if (!(other instanceof Release)) {
            return false;
        }
        return id.equals(((Release)other).id);
    }

    @Override
    public boolean hasSameContent(@NotNull BindableRecyclerAdapter.Diffable other) {
        Release otherRelease = (Release) other;
        if (!tag.equals(otherRelease.tag)) return false;
        if (!body.equals(otherRelease.body)) return false;
        return true;
    }
}
