package com.eticum.api.http.transport.request;

import com.eticum.api.http.model.Action;
import com.eticum.api.http.model.LogItem;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class LogRequest {

    @JsonProperty("action")
    private final Action action = Action.log;

    @JsonProperty("accessToken")
    private String accessToken;

    @JsonProperty("data")
    private List<LogItem> data;
}
