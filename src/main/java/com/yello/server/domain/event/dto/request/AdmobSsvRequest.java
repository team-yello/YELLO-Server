package com.yello.server.domain.event.dto.request;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import lombok.Builder;

@Builder
public record AdmobSsvRequest(
    String customData,
    String signature,
    Long keyId,
    String transactionId,
    String rewardItem,
    Integer rewardAmount

) {
    public static AdmobSsvRequest of(Map<String, String[]> parameters ) {
        Function<String, String> getParameter = (key) ->
            Optional.ofNullable(parameters.get(key))
                .flatMap(arr -> Arrays.stream(arr).findFirst())
                .orElse("");

        long keyId = Optional.ofNullable(parameters.get("key_id"))
            .flatMap(arr -> Arrays.stream(arr).findFirst())
            .map(Long::parseLong)
            .orElse(0L);

        int rewardAmount = Optional.ofNullable(parameters.get("reward_amount"))
            .flatMap(arr -> Arrays.stream(arr).findFirst())
            .map(Integer::parseInt)
            .orElse(0);

        return AdmobSsvRequest.builder()
            .customData(getParameter.apply("customData"))
            .signature(getParameter.apply("signature"))
            .keyId(keyId)
            .transactionId(getParameter.apply("transaction_id"))
            .rewardItem(getParameter.apply("reward_item"))
            .rewardAmount(rewardAmount)
            .build();
    }

}
