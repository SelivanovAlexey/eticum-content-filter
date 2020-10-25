package com.eticum.api.http.transport.request;

import com.eticum.api.http.model.Action;
import com.eticum.api.http.model.Language;
import com.eticum.api.http.transport.response.GenericResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Setter
public class AuthRequest {
    @JsonProperty("action")
    private final Action action = Action.auth;

    @JsonProperty("accessToken")
    private String accessToken;

    @JsonProperty("refreshToken")
    private String refreshToken;

    @JsonProperty("login")
    private String login;

    @JsonProperty("password")
    private String password;

    @JsonProperty("profileHash")
    private String profileHash;

    @JsonProperty("clientId")
    private String clientId;

    @JsonProperty("deviceName")
    private String deviceName;

    @JsonProperty("lang")
    private Language language;
}
