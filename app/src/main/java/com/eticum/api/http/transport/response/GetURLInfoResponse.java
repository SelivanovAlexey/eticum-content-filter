package com.eticum.api.http.transport.response;

import com.eticum.api.http.model.Info;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class GetURLInfoResponse extends GenericResponse {
    @JsonProperty("info")
    private Info info;

}
