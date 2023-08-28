package com.yello.server.global.common.factory;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.security.KeyFactory;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Date;
import lombok.SneakyThrows;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TokenFactoryImpl implements TokenFactory {

    @Value("${kid}")
    private String kid;

    @Value("${iss}")
    private String iss;

    @Value("${aud}")
    private String aud;

    @Value("${bid}")
    private String bid;

    @Value("${sig}")
    private String sig;


    @SneakyThrows
    @Override
    public String generateAppleToken() {
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


}
