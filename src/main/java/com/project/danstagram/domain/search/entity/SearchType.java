package com.project.danstagram.domain.search.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum SearchType {

    HASHTAG,
    ACCOUNT,
    PLACE;


    @JsonCreator
    public static SearchType fromString(String key) {
        return key == null ? null : SearchType.valueOf(key.toUpperCase());
    }

    @JsonValue
    public String toString() {
        return name().toLowerCase();
    }
}