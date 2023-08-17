package com.yello.server.global.common.factory;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.security.KeyFactory;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Calendar;
import java.util.Date;
import lombok.SneakyThrows;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TokenFactory {

    @Value("${kid}")
    private String KID;
    @Value("${iss}")
    private String ISS;
    @Value("${aud}")
    private String AUD;
    @Value("${bid}")
    private String BID;
    @Value("${sig}")
    private String SIG;

    @SneakyThrows
    public String generateAppleToken() {
        String jws = Jwts.builder()
            // header
            .setHeaderParam("kid", KID)
            // payload
            .setIssuer(ISS)
            .setIssuedAt(new Date(Calendar.getInstance().getTimeInMillis()))    // 발행 시간 - UNIX 시간
            .setExpiration(
                new Date(Calendar.getInstance().getTimeInMillis() + (3 * 60
                    * 1000)))    // 만료 시간 (발행 시간 + 3분)
            .setAudience(AUD)
            .claim("bid", BID)
            // sign
            .signWith(SignatureAlgorithm.ES256,
                KeyFactory.getInstance("EC").generatePrivate(new PKCS8EncodedKeySpec(
                    Base64.decodeBase64(SIG))))
            .compact();

        return jws;
    }
}
