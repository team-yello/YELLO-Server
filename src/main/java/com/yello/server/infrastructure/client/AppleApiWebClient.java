package com.yello.server.infrastructure.client;

import static com.yello.server.global.common.ErrorCode.NOT_FOUND_TRANSACTION_EXCEPTION;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.yello.server.domain.purchase.dto.apple.AppleTransaction;
import com.yello.server.domain.purchase.dto.apple.TransactionInfoResponse;
import com.yello.server.domain.purchase.exception.PurchaseException;
import com.yello.server.global.common.factory.TokenFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class AppleApiWebClient implements ApiWebClient {

    private final TokenFactory tokenFactory;
    @Value("${apple.production.uri}")
    private String APPLE_PRODUCTION_URL;
    @Value("${apple.sandbox.uri}")
    private String APPLE_SANDBOX_URL;


    @Override
    public ResponseEntity<TransactionInfoResponse> appleGetTransaction(
        AppleTransaction appleTransaction) {
        ResponseEntity<TransactionInfoResponse> transactionResponse =
            getTransactionByWebClient(appleTransaction, APPLE_PRODUCTION_URL);

        HttpStatus statusCode = transactionResponse.getStatusCode();

        if (transactionResponse==null) {
            throw new PurchaseException(NOT_FOUND_TRANSACTION_EXCEPTION);
        }
        if (statusCode.equals(HttpStatus.NOT_FOUND)) {
            return getTransactionByWebClient(appleTransaction, APPLE_SANDBOX_URL);
        }

        return transactionResponse;
    }

    @Override
    public ResponseEntity<TransactionInfoResponse> getTransactionByWebClient(
        AppleTransaction appleTransaction, String appleUrl) {
        String appleToken = tokenFactory.generateAppleToken();

        WebClient webClient = WebClient.builder()
            .defaultHeader(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE)
            .defaultHeader(HttpHeaders.AUTHORIZATION, appleToken)
            .build();

        return webClient.get()
            .uri(appleUrl + "/{transactionId}", appleTransaction.transactionId())
            .exchangeToMono(
                clientResponse -> clientResponse.toEntity(TransactionInfoResponse.class))
            .block();
    }
}
