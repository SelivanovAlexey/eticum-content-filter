package com.eticum.api.http.transport.request;

import com.eticum.api.http.model.Action;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class GetURLInfoRequest {

    @JsonProperty("action")
    private final Action action = Action.info;

    @JsonProperty("accessToken")
    private String accessToken;

    @JsonProperty("url")
    private String url;
}
