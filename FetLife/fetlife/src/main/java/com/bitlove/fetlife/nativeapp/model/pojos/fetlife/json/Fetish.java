package com.bitlove.fetlife.nativeapp.model.pojos.fetlife.json;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Fetish {

    @JsonProperty("approved")
    private boolean approved;

    @JsonProperty("created_at")
    private String createdAt;

    @JsonProperty("id")
    private int id;

    @JsonProperty("into_count")
    private int intoCount;

    @JsonProperty("name")
    private String name;

    @JsonProperty("people_intos_count")
    private int peopleIntosCount;

    @JsonProperty("updated_at")
    private String updatedAt;

    @JsonProperty("url")
    private String url;


    @JsonProperty("created_at")
    public String getCreatedAt() {
        return createdAt;
    }

    @JsonProperty("created_at")
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    @JsonProperty("id")
    public int getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(int id) {
        this.id = id;
    }

    @JsonProperty("into_count")
    public int getIntoCount() {
        return intoCount;
    }

    @JsonProperty("into_count")
    public void setIntoCount(int intoCount) {
        this.intoCount = intoCount;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("people_intos_count")
    public int getPeopleIntosCount() {
        return peopleIntosCount;
    }

    @JsonProperty("people_intos_count")
    public void setPeopleIntosCount(int peopleIntosCount) {
        this.peopleIntosCount = peopleIntosCount;
    }

    @JsonProperty("updated_at")
    public String getUpdatedAt() {
        return updatedAt;
    }

    @JsonProperty("updated_at")
    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    @JsonProperty("url")
    public String getUrl() {
        return url;
    }

    @JsonProperty("url")
    public void setUrl(String url) {
        this.url = url;
    }

    @JsonProperty("approved")
    public boolean isApproved() {
        return approved;
    }

    @JsonProperty("approved")
    public void setApproved(boolean approved) {
        this.approved = approved;
    }

}
