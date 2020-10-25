package com.eticum.api.http.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
public class Category {

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("name")
    private String name;
}
