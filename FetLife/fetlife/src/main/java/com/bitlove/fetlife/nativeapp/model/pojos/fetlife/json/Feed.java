
package com.bitlove.fetlife.nativeapp.model.pojos.fetlife.json;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class Feed {

    @JsonProperty("stories")
    private List<Story> stories = new ArrayList<Story>();

    @JsonProperty("stories")
    public List<Story> getStories() {
        return stories;
    }

    @JsonProperty("stories")
    public void setStories(List<Story> stories) {
        this.stories = stories;
    }

}
