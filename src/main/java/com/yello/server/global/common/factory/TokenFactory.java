package com.yello.server.global.common.factory;

import com.yello.server.domain.purchase.dto.response.AppleJwsTransactionResponse;

public interface TokenFactory {

    String generateAppleToken();

    AppleJwsTransactionResponse decodeTransactionToken(String signedTransactionInfo);
}
