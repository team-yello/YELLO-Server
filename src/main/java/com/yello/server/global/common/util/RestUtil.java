package com.yello.server.global.common.util;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.yello.server.domain.authorization.dto.kakao.KakaoFriendsInfo;
import com.yello.server.domain.authorization.dto.kakao.KakaoTokenInfo;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;

public class RestUtil {

    private static final String KAKAO_TOKEN_INFO_URL = "https://kapi.kakao.com/v1/user/access_token_info";
    private static final String KAKAO_FRIEND_LIST_URL = "https://kapi.kakao.com/v1/api/talk/friends";

    private RestUtil() {
        throw new IllegalStateException();
    }

    public static ResponseEntity<KakaoTokenInfo> getKakaoTokenInfo(String kakaoAccessToken) {
        WebClient webClient = WebClient.builder()
            .baseUrl(KAKAO_TOKEN_INFO_URL)
            .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + kakaoAccessToken)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE)
            .build();

        return webClient.get()
            .exchangeToMono(clientResponse -> clientResponse.toEntity(KakaoTokenInfo.class))
            .block();
    }

    public static ResponseEntity<KakaoFriendsInfo> getKakaoFriendList(String kakaoAccessToken, String friendOrder,
        Integer offset, Integer limit, String order) {
        String baseUrl = KAKAO_FRIEND_LIST_URL + "?";
        if (friendOrder!=null) {
            baseUrl += "friendOrder=" + friendOrder;
        }
        if (offset!=null) {
            baseUrl += "offset=" + offset;
        }
        if (limit!=null) {
            baseUrl += "limit=" + limit;
        }
        if (order!=null) {
            baseUrl += "order=" + order;
        }
        if (baseUrl.equals(KAKAO_FRIEND_LIST_URL + "?")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        }

        WebClient webClient = WebClient.builder()
            .baseUrl(baseUrl)
            .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + kakaoAccessToken)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE)
            .build();

        return webClient.get()
            .exchangeToMono(clientResponse -> clientResponse.toEntity(KakaoFriendsInfo.class))
            .block();
    }
}
