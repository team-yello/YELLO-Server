package com.yello.server.global.common.util;

import com.yello.server.global.common.dto.kakao.KakaoTokenInfo;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;

public class RestUtil {
    private static String KAKAO_TOKEN_INFO_URL = "https://kapi.kakao.com/v1/user/access_token_info";

    public static ResponseEntity<KakaoTokenInfo> getKakaoTokenInfo(String kakaoAccessToken){
        WebClient webClient = WebClient.builder()
                .baseUrl(KAKAO_TOKEN_INFO_URL)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + kakaoAccessToken)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();

        ResponseEntity<KakaoTokenInfo> response = webClient.get()
                .exchangeToMono(clientResponse -> clientResponse.toEntity(KakaoTokenInfo.class))
                .block();

        return response;
    }
}
