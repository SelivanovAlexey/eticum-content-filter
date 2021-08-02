package com.eticum.api.http.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;

@AllArgsConstructor
@Builder
public class LogItem {

    @JsonProperty("t")
    private Long timestamp;

    @JsonProperty("u")
    private String url;

    @JsonProperty("r")
    private Integer visitResult;

    @JsonProperty("h")
    private String hash;
}
