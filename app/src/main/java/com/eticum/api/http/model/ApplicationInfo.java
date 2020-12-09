package com.eticum.api.http.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Value;

@Value
@Builder
public class ApplicationInfo {

    @JsonProperty("p")
    String appPackage;

    @JsonProperty("n")
    String appName;
}
