package com.yello.server.domain.user.entity;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Gender {
    MALE("M"),
    FEMALE("F");

    private final String intial;

    public String intial() {
        return intial;
    }
}
