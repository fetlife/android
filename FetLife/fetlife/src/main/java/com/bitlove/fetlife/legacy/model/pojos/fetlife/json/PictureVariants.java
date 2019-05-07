
package com.bitlove.fetlife.legacy.model.pojos.fetlife.json;

import com.bitlove.fetlife.legacy.model.pojos.fetlife.MediaVariantsInterface;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PictureVariants implements MediaVariantsInterface {

    @JsonProperty("150")
    private PictureVariant _150;

    @JsonProperty("345")
    private PictureVariant _345;

    @JsonProperty("80")
    private PictureVariant _80;

    @JsonProperty("huge")
    private PictureVariant huge;

    @JsonProperty("large")
    private PictureVariant large;

    @JsonProperty("medium")
    private PictureVariant medium;

    @JsonProperty("original")
    private PictureVariant original;


    //Transform methods
    @Override
    public String getLargeUrl() {
        return large != null ? large.getUrl() : null;
    }

    @Override
    public String getMediumUrl() {
        return medium != null ? medium.getUrl() : null;
    }

    @Override
    public String getHugeUrl() {
        return huge != null ? huge.getUrl() : null;
    }


    //Getters/Setters
    @JsonProperty("150")
    public PictureVariant get150() {
        return _150;
    }

    @JsonProperty("150")
    public void set150(PictureVariant _150) {
        this._150 = _150;
    }

    @JsonProperty("345")
    public PictureVariant get345() {
        return _345;
    }

    @JsonProperty("345")
    public void set345(PictureVariant _345) {
        this._345 = _345;
    }

    @JsonProperty("80")
    public PictureVariant get80() {
        return _80;
    }

    @JsonProperty("80")
    public void set80(PictureVariant _80) {
        this._80 = _80;
    }

    @JsonProperty("huge")
    public PictureVariant getHuge() {
        return huge;
    }

    @JsonProperty("huge")
    public void setHuge(PictureVariant huge) {
        this.huge = huge;
    }

    @JsonProperty("large")
    public PictureVariant getLarge() {
        return large;
    }

    @JsonProperty("large")
    public void setLarge(PictureVariant large) {
        this.large = large;
    }

    @JsonProperty("medium")
    public PictureVariant getMedium() {
        return medium;
    }

    @JsonProperty("medium")
    public void setMedium(PictureVariant medium) {
        this.medium = medium;
    }

    @JsonProperty("original")
    public PictureVariant getOriginal() {
        return original;
    }

    @JsonProperty("original")
    public void setOriginal(PictureVariant original) {
        this.original = original;
    }
}
