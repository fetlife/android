package com.bitlove.fetlife.nativeapp.model.pojos.fetlife.json;

import com.bitlove.fetlife.nativeapp.model.pojos.fetlife.dbjson.Picture;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MessageEntities {

    @JsonProperty("mentions")
    private List<Mention> mentions = new ArrayList<>();

    @JsonProperty("pictures")
    private List<Picture> pictures = new ArrayList<>();

    public List<Mention> getMentions() {
        return mentions;
    }

    public void setMentions(List<Mention> mentions) {
        this.mentions = mentions;
    }

    public List<Picture> getPictures() {
        return pictures;
    }

    public void setPictures(List<Picture> pictures) {
        this.pictures = pictures;
    }
}
