package com.bitlove.fetlife.model.pojos;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "marker",
        "no_more",
        "show_cta",
        "stories"
})
public class Feed {

    @JsonProperty("marker")
    private int marker;
    @JsonProperty("no_more")
    private boolean noMore;
    @JsonProperty("show_cta")
    private boolean showCta;
    @JsonProperty("stories")
    private List<Story> stories = new ArrayList<Story>();
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     *
     * @return
     * The marker
     */
    @JsonProperty("marker")
    public int getMarker() {
        return marker;
    }

    /**
     *
     * @param marker
     * The marker
     */
    @JsonProperty("marker")
    public void setMarker(int marker) {
        this.marker = marker;
    }

    /**
     *
     * @return
     * The noMore
     */
    @JsonProperty("no_more")
    public boolean isNoMore() {
        return noMore;
    }

    /**
     *
     * @param noMore
     * The no_more
     */
    @JsonProperty("no_more")
    public void setNoMore(boolean noMore) {
        this.noMore = noMore;
    }

    /**
     *
     * @return
     * The showCta
     */
    @JsonProperty("show_cta")
    public boolean isShowCta() {
        return showCta;
    }

    /**
     *
     * @param showCta
     * The show_cta
     */
    @JsonProperty("show_cta")
    public void setShowCta(boolean showCta) {
        this.showCta = showCta;
    }

    /**
     *
     * @return
     * The stories
     */
    @JsonProperty("stories")
    public List<Story> getStories() {
        return stories;
    }

    /**
     *
     * @param stories
     * The stories
     */
    @JsonProperty("stories")
    public void setStories(List<Story> stories) {
        this.stories = stories;
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

