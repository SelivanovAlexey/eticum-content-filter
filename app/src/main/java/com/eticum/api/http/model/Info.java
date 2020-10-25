package com.eticum.api.http.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
public class Info {

    @JsonProperty("status")
    private String status;

    @JsonProperty("url")
    private String url;

    @JsonProperty("age")
    private Integer age;

    @JsonProperty("categories")
    private List<Integer> categories;

    @JsonProperty("cnt")
    private Integer count;

    @JsonProperty("reputation")
    private Integer reputation;

    @JsonProperty("pages")
    private Integer pages;
}
