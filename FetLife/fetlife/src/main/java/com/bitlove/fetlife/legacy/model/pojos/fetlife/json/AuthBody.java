package com.bitlove.fetlife.legacy.model.pojos.fetlife.json;

import com.bitlove.fetlife.legacy.model.api.FetLifeService;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AuthBody {

    @JsonProperty("grant_type")
    private String grantType;

    @JsonProperty("username")
    private String username;

    @JsonProperty("password")
    private String password;

    public AuthBody(String username, String password) {
        this.grantType = FetLifeService.GRANT_TYPE_PASSWORD;
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getGrantType() {
        return grantType;
    }

    public void setGrantType(String grantType) {
        this.grantType = grantType;
    }


}
