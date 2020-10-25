package com.eticum.api.http.transport.response;

import com.eticum.api.http.model.Profile;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class KeepAliveResponse extends GenericResponse {
    @JsonProperty("profileHash")
    private String profileHash;

    @JsonProperty("profile")
    private Profile profile;
}
