package com.yello.server.global.common.factory;

public interface TokenFactory {

    String generateAppleToken();

    void decodeTransactionToken(String signedTransactionInfo,
        String transactionId);
}
