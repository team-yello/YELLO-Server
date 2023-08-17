package com.yello.server.global.common.dto.response;

public record GoogleInAppGetResponse(
    String purchaseTimeMillis,
    Integer purchaseState,
    Integer consumptionState,
    String developerPayload,
    String orderId,
    Integer purchaseType,
    Integer acknowledgementState,
    String kind,
    String regionCode
) {

}