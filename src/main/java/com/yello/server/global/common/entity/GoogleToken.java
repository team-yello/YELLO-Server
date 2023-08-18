package com.yello.server.global.common.entity;

import com.yello.server.global.common.dto.AuditingTimeEntity;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GoogleToken extends AuditingTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String accessToken;

    String refreshToken;

    public void updateAccessToken(String newAccessToken) {
        this.accessToken = newAccessToken;
    }
}
