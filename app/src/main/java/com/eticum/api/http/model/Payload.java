package com.eticum.api.http.model;


import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Payload {
    @JsonProperty("error")
    Integer error;
}
