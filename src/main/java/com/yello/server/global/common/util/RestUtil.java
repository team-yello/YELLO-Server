package com.yello.server.global.common.util;

import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.yello.server.domain.authorization.dto.kakao.KakaoTokenInfo;
import com.yello.server.global.common.dto.response.GoogleInAppGetResponse;
import com.yello.server.global.common.dto.response.GoogleTokenIssueResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;
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

        WebClient webClient = WebClient.builder()
            .baseUrl(GOOGLE_TOKEN_ISSUE_URL)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, APPLICATION_FORM_URLENCODED_VALUE)
            .build();

        return webClient.post()
            .bodyValue(formData)
            .exchangeToMono(clientResponse -> clientResponse.toEntity(GoogleTokenIssueResponse.class))
            .block();
    }

    public static ResponseEntity<String> getGoogleSubscribeCheck(String purchaseToken,
        String accessToken) {
        WebClient webClient = WebClient.builder()
            .baseUrl(GOOGLE_PLAY_SUBSCRIPTIONS_GET_PATH(ANDROID_PACKAGE_NAME, purchaseToken))
            .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
            .build();

        return webClient.get()
            .exchangeToMono(clientResponse -> clientResponse.toEntity(String.class))
            .block();
    }

    public static ResponseEntity<GoogleInAppGetResponse> getGoogleTicketCheck(String ticketType, String purchaseToken,
        String accessToken) {
        WebClient webClient = WebClient.builder()
            .baseUrl(GOOGLE_PLAY_INAPP_GET_PATH(ANDROID_PACKAGE_NAME, ticketType, purchaseToken))
            .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
            .build();

        return webClient.get()
            .exchangeToMono(clientResponse -> clientResponse.toEntity(GoogleInAppGetResponse.class))
            .block();
    }

    public static String getRequestBody(HttpServletRequest request) throws IOException {
        if (!(request instanceof SecurityContextHolderAwareRequestWrapper)) {
            throw new IllegalArgumentException("MultiReadHttpServletRequest 설정을 확인하세요");
        }

        String body = null;
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = null;

        try {
            InputStream inputStream = request.getInputStream();
            if (inputStream != null) {
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                char[] charBuffer = new char[128];
                int bytesRead = -1;
                while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
                    stringBuilder.append(charBuffer, 0, bytesRead);
                }
            }
        } catch (IOException ex) {
            throw ex;
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException ex) {
                    throw ex;
                }
            }
        }

        body = stringBuilder.toString();
        return body;
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
