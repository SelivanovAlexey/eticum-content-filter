package com.eticum.api.http.model;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Set;

import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class Payload {
    @JsonProperty("error")
    Integer error;

    @JsonProperty("installedApps")
    Set<ApplicationInfo> apps;
}
