package com.eticum.api.http.transport.response;

import com.eticum.api.http.model.Payload;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NonNull;

@Getter
public abstract class GenericResponse {
    @JsonProperty("action")
    protected String action;

    @JsonProperty("status")
    @NonNull
    protected String status;

    @JsonProperty("payload")
    protected Payload payload;
}
