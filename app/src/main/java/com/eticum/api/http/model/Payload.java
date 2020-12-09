package com.eticum.api.http.model;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Set;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class Payload {
    @JsonProperty("error")
    Integer error;

    @JsonProperty("installedApps")
    @NonNull
    Set<ApplicationInfo> apps;
}
