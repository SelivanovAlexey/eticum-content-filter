package com.eticum.api.http.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ProfileSrc {
    @JsonProperty("default")
    DEFAULT("default"),
    @JsonProperty("device")
    DEVICE("device"),
    @JsonProperty("group")
    GROUP("group"),
    @JsonProperty("timetable")
    TIMETABLE("timetable"),
    @JsonProperty("groupTimetable")
    GROUPTIMETABLE("groupTimetable");

    private final String toString;

    ProfileSrc(String toString) {
        this.toString = toString;
    }

    public String toString(){
        return toString;
    }
}
