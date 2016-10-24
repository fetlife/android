package com.bitlove.fetlife.model.pojos;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "approved",
        "created_at",
        "id",
        "into_count",
        "name",
        "people_intos_count",
        "updated_at"
})
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
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     *
     * @return
     * The approved
     */
    @JsonProperty("approved")
    public boolean isApproved() {
        return approved;
    }

    /**
     *
     * @param approved
     * The approved
     */
    @JsonProperty("approved")
    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    /**
     *
     * @return
     * The createdAt
     */
    @JsonProperty("created_at")
    public String getCreatedAt() {
        return createdAt;
    }

    /**
     *
     * @param createdAt
     * The created_at
     */
    @JsonProperty("created_at")
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    /**
     *
     * @return
     * The id
     */
    @JsonProperty("id")
    public int getId() {
        return id;
    }

    /**
     *
     * @param id
     * The id
     */
    @JsonProperty("id")
    public void setId(int id) {
        this.id = id;
    }

    /**
     *
     * @return
     * The intoCount
     */
    @JsonProperty("into_count")
    public int getIntoCount() {
        return intoCount;
    }

    /**
     *
     * @param intoCount
     * The into_count
     */
    @JsonProperty("into_count")
    public void setIntoCount(int intoCount) {
        this.intoCount = intoCount;
    }

    /**
     *
     * @return
     * The name
     */
    @JsonProperty("name")
    public String getName() {
        return name;
    }

    /**
     *
     * @param name
     * The name
     */
    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @return
     * The peopleIntosCount
     */
    @JsonProperty("people_intos_count")
    public int getPeopleIntosCount() {
        return peopleIntosCount;
    }

    /**
     *
     * @param peopleIntosCount
     * The people_intos_count
     */
    @JsonProperty("people_intos_count")
    public void setPeopleIntosCount(int peopleIntosCount) {
        this.peopleIntosCount = peopleIntosCount;
    }

    /**
     *
     * @return
     * The updatedAt
     */
    @JsonProperty("updated_at")
    public String getUpdatedAt() {
        return updatedAt;
    }

    /**
     *
     * @param updatedAt
     * The updated_at
     */
    @JsonProperty("updated_at")
    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
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
