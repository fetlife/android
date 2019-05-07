package com.bitlove.fetlife.legacy.model.pojos.fetlife.json;

import com.bitlove.fetlife.legacy.model.pojos.fetlife.dbjson.Member;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Mention {

    @JsonProperty("offset")
    private int offset;

    @JsonProperty("length")
    private int length;

    @JsonProperty("member")
    private Member member;

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }
}
