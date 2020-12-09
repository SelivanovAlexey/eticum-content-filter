package com.eticum.api.http.transport.request;

import com.eticum.api.http.model.Action;
import com.eticum.api.http.model.Application;
import com.eticum.api.http.model.Payload;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Setter
public class KeepAliveRequest {

    @JsonProperty("action")
    private final Action action = Action.online;

    @JsonProperty("accessToken")
    @NonNull
    private String accessToken;

    @JsonProperty("profileHash")
    private String profileHash;

    @JsonProperty("appName")
    private Application application;

    @JsonProperty("appVersion")
    private Application applicationVersion;

    @JsonProperty("platform")
    private String platform;

    @JsonProperty("payload")
    @NonNull
    private Payload payload;

}
