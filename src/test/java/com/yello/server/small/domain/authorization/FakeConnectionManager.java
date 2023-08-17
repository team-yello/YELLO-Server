package com.yello.server.small.domain.authorization;

import com.yello.server.domain.authorization.dto.kakao.KakaoTokenInfo;
import com.yello.server.global.common.manager.ConnectionManager;
import org.springframework.http.ResponseEntity;

public class FakeConnectionManager implements ConnectionManager {

    @Override
    public ResponseEntity<KakaoTokenInfo> getKakaoTokenInfo(String kakaoAccessToken) {
        return null;
    }
}
