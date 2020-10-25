package com.eticum.api.http.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class User {

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("email")
    private String email;

    @JsonProperty("org")
    private String org;

    @JsonProperty("tel")
    private String mobileNumber;

    @JsonProperty("timeOffset")
    private String timeOffset;

    @JsonProperty("name")
    private String name;

    @JsonProperty("reputation")
    private String reputation;

    @JsonProperty("subscriptionTill")
    private Date subscriptionTill;

    @JsonProperty("interactiveMode")
    private boolean interactiveMode;

    @JsonProperty("organization")
    private String organization;

    @JsonProperty("position")
    private String position;

    @JsonProperty("contributor")
    private String contributor;

}
