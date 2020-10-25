package com.eticum.api.http.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Set;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@NoArgsConstructor
@RequiredArgsConstructor
@Getter
public class Profile {

    @JsonProperty("id")
    @NonNull
    private Integer id;

    @JsonProperty("name")
    @NonNull
    private String name;

    @JsonProperty("profileSrc")
    @NonNull
    private ProfileSrc profileSrc;

    @JsonProperty("mode")
    @NonNull
    private Mode mode;

    @JsonProperty("age")
    @NonNull
    private Integer age;

    @JsonProperty("categories")
    @NonNull
    private List<Integer> categories;

    @JsonProperty("allow")
    @NonNull
    private List<String> allowUrls;

    @JsonProperty("deny")
    @NonNull
    private List<String> denyUrls;

    @JsonProperty("filterWords")
    @NonNull
    private boolean filterWords;

    @JsonProperty("logURL")
    @NonNull
    private boolean logURL;

    @JsonProperty("logActivity")
    @NonNull
    private boolean logActivity;

    @JsonProperty("apps")
    @NonNull
    private Set<String> apps;

//    @JsonProperty("words")
//    private List<String> words;

    @JsonProperty("interactive")
    private String interactive;
}




