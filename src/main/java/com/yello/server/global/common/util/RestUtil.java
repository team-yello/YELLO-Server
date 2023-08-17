package com.yello.server.global.common.util;

import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.yello.server.domain.authorization.dto.kakao.KakaoTokenInfo;
import com.yello.server.global.common.dto.response.GoogleInAppGetResponse;
import com.yello.server.global.common.dto.response.GoogleTokenIssueResponse;
import java.io.IOException;
import java.io.InputStreamReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class RestUtil {

    private static final String KAKAO_TOKEN_INFO_URL =
        "https://kapi.kakao.com/v1/user/access_token_info";
    private static final String KAKAO_FRIEND_LIST_URL =
        "https://kapi.kakao.com/v1/api/talk/friends";
    private static final String GOOGLE_TOKEN_ISSUE_URL = "https://oauth2.googleapis.com/token";
    private static String GOOGLE_CLIENT_SECRET_PATH;
    private static String ANDROID_PACKAGE_NAME;

    private static String GOOGLE_PLAY_SUBSCRIPTIONS_GET_PATH(String packageName, String purchaseToken) {
        return "https://androidpublisher.googleapis.com/androidpublisher/v3/applications/"
            + packageName + "/purchases/subscriptionsv2/tokens/"
            + purchaseToken;
    }

    private static String GOOGLE_PLAY_INAPP_GET_PATH(String packageName, String ticketType, String purchaseToken) {
        return "https://androidpublisher.googleapis.com/androidpublisher/v3/applications/"
            + packageName + "/purchases/products/"
            + ticketType + "/tokens/"
            + purchaseToken;
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

    public static ResponseEntity<GoogleTokenIssueResponse> postGoogleTokenReissue(String refreshToken)
        throws IOException {
        Gson gson = new Gson();
        ClassPathResource resource = new ClassPathResource(GOOGLE_CLIENT_SECRET_PATH);
        JsonObject object = gson.fromJson(new InputStreamReader(resource.getInputStream()), JsonObject.class);
        object = (JsonObject) object.get("web");

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        String clientSecret = String.valueOf(object.get("client_secret"));
        String clientId = String.valueOf(object.get("client_id"));

        formData.add("client_secret", clientSecret.replaceAll("\"", ""));
        formData.add("client_id", clientId.replaceAll("\"", ""));
        formData.add("grant_type", "refresh_token");
        formData.add("refresh_token", refreshToken);
        System.out.println("formData = " + formData);
        WebClient webClient = WebClient.builder()
            .baseUrl(GOOGLE_TOKEN_ISSUE_URL)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, APPLICATION_FORM_URLENCODED_VALUE)
            .build();

        return webClient.post()
            .bodyValue(formData)
            .exchangeToMono(clientResponse -> clientResponse.toEntity(GoogleTokenIssueResponse.class))
            .block();
    }

    public static ResponseEntity<String> getSubscribeCheck(String purchaseToken,
        String accessToken) {
        WebClient webClient = WebClient.builder()
            .baseUrl(GOOGLE_PLAY_SUBSCRIPTIONS_GET_PATH(ANDROID_PACKAGE_NAME, purchaseToken))
            .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
            .build();

        return webClient.get()
            .exchangeToMono(clientResponse -> clientResponse.toEntity(String.class))
            .block();
    }

    public static ResponseEntity<GoogleInAppGetResponse> getTicketCheck(String ticketType, String purchaseToken,
        String accessToken) {
        WebClient webClient = WebClient.builder()
            .baseUrl(GOOGLE_PLAY_INAPP_GET_PATH(ANDROID_PACKAGE_NAME, ticketType, purchaseToken))
            .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
            .build();

        return webClient.get()
            .exchangeToMono(clientResponse -> clientResponse.toEntity(GoogleInAppGetResponse.class))
            .block();
    }

    @Value("${google.developer.key}")
    public void setGoogleClientSecretPath(String value) {
        GOOGLE_CLIENT_SECRET_PATH = value;
    }

    @Value("${google.android.package-name}")
    public void setAndroidPackageName(String value) {
        ANDROID_PACKAGE_NAME = value;
    }
}
