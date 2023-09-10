package com.yello.server.global.common.factory;

import static com.yello.server.global.common.ErrorCode.NOT_EQUAL_TRANSACTION_EXCEPTION;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.yello.server.domain.purchase.exception.PurchaseNotFoundException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.KeyFactory;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Date;
import java.util.Map;
import lombok.SneakyThrows;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

@Component
public class TokenFactoryImpl implements TokenFactory {

    private String kid;
    private String iss;
    private String aud;
    private String bid;
    private String sig;

    @Value("${apple.key}")
    private String appleConfigPath;

    @SneakyThrows
    @Override
    public String generateAppleToken() {
        setKey();
        return Jwts.builder()
            .setHeaderParam("kid", kid)
            .setIssuer(iss)
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + (3 * 60 * 1000)))
            .setAudience(aud)
            .claim("bid", bid)
            .signWith(
                SignatureAlgorithm.ES256,
                KeyFactory.getInstance("EC")
                    .generatePrivate(new PKCS8EncodedKeySpec(Base64.decodeBase64(sig)))
            )
            .compact();
    }

    @SneakyThrows
    @Override
    public void decodeTransactionToken(String signedTransactionInfo,
        String transactionId) {

        Map<String, Object> decodeToken = DecodeTokenFactory.decodeToken(signedTransactionInfo);

        String decodeTransactionId = decodeToken.get("transactionId").toString();
        if (!transactionId.equals(decodeTransactionId)) {
            throw new PurchaseNotFoundException(NOT_EQUAL_TRANSACTION_EXCEPTION);
        }

    }

    public void setKey() throws IOException {
        Gson gson = new Gson();
        ClassPathResource resource = new ClassPathResource(appleConfigPath);
        JsonObject object =
            gson.fromJson(new InputStreamReader(resource.getInputStream()), JsonObject.class);
        kid = String.valueOf(object.get("kid")).replaceAll("\"", "");
        iss = String.valueOf(object.get("iss")).replaceAll("\"", "");
        aud = String.valueOf(object.get("aud")).replaceAll("\"", "");
        bid = String.valueOf(object.get("bid")).replaceAll("\"", "");
        sig = String.valueOf(object.get("sig-auth-key")).replaceAll("\"", "")
            .replaceAll("\\\\n", "\n");
    }


}
