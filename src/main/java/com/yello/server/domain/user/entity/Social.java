package com.yello.server.domain.user.entity;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Social {
    KAKAO("Kakao"),
    APPLE("Apple");

    private final String social;

    public String social() {
        return social;
    }
}
