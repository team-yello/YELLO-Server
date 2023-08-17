package com.yello.server.global.common.manager;

import com.yello.server.domain.authorization.dto.kakao.KakaoTokenInfo;
import org.springframework.http.ResponseEntity;

public interface ConnectionManager {


    ResponseEntity<KakaoTokenInfo> getKakaoTokenInfo(String kakaoAccessToken);

}
