package com.eticum.api.http.transport.response;

import com.eticum.api.http.model.Category;
import com.eticum.api.http.model.Payload;
import com.eticum.api.http.model.Profile;
import com.eticum.api.http.model.User;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class AuthResponse extends GenericResponse {
    @JsonProperty("user")
    private User user;

    @JsonProperty("categories")
    private List<Category> categories;

    @JsonProperty("accessToken")
    private String accessToken;

    @JsonProperty("refreshToken")
    private String refreshToken;

    @JsonProperty("clientId")
    private String clientId;

    @JsonProperty("profileHash")
    private String profileHash;

    @JsonProperty("profile")
    private Profile profile;

    @JsonProperty("deviceName")
    private String deviceName;

}
