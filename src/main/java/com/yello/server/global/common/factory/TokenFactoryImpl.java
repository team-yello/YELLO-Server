package com.yello.server.global.common.factory;

import static java.util.Base64.getDecoder;
import static java.util.Base64.getMimeDecoder;

import com.yello.server.domain.purchase.dto.response.AppleJwsTransactionResponse;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
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


    private static PrivateKey getPrivateKeyFromPem(String privateKeyBytes)
        throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] decode = getMimeDecoder().decode(privateKeyBytes);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decode);

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        System.out.println(keyFactory.generatePrivate(keySpec) + " sdfsdfsdfs");
        return keyFactory.generatePrivate(keySpec);
    }

    private static RSAPublicKey getPublicKeyFromPem(String publicKeyBytes)
        throws NoSuchAlgorithmException, InvalidKeySpecException {

        System.out.println(publicKeyBytes + "  dddddddd");
        byte[] encoded = getMimeDecoder().decode(publicKeyBytes);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encoded);
        System.out.println(encoded + " ddsdfsdfss");

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        RSAPublicKey pubKey =
            (RSAPublicKey) keyFactory.generatePublic(keySpec);
        System.out.println(pubKey + " sdfsdfsddddfs");
        return pubKey;
    }

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

    @SneakyThrows
    @Override
    public AppleJwsTransactionResponse decodeTransactionToken(String signedTransactionInfo) {
        String publicKeyPem =
            "MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEnWMSCXOIO30RT6uO/gftB3vMIk7J\n"
                + "B46aGm4OglC1fAJFxrpFVHkKMeH+onROoJMtmel4POfh6VADeKQdtddQIA==";
        String privateKeyPem =
            "MIGTAgEAMBMGByqGSM49AgEGCCqGSM49AwEHBHkwdwIBAQQgFb1FLrKi9PvnQ9/x\n"
                + "rSHp2WtTq8kJ663R9ctCm9JKO5OgCgYIKoZIzj0DAQehRANCAATolS9U3fxQyN2N\n"
                + "x/1DFHRTQucP/4ndvBwLxrS9RoxT3gFoChT31r98O8dgVj3VlUPU32WDlpdowglA\n"
                + "XZKAWIVY";
        //byte[] publicKeyBytes = getDecoder().decode(publicKeyPem);
        //byte[] privateKeyBytes = getDecoder().decode(privateKeyPem);

        RSAPublicKey publicKey = getPublicKeyFromPem(publicKeyPem);
        PrivateKey privateKey = getPrivateKeyFromPem(privateKeyPem);

        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initVerify(publicKey);
        signature.update(signedTransactionInfo.getBytes());

        byte[] signatureBytes =
            getDecoder().decode(signedTransactionInfo.split("\\.")[2]);
        boolean isValid = signature.verify(signatureBytes);

        if (isValid) {
            Claims claims = Jwts.parserBuilder()
                .setSigningKey(publicKey)
                .build()
                .parseClaimsJws(signedTransactionInfo)
                .getBody();
            return AppleJwsTransactionResponse.of(claims);
        }

        return null;
    }


}
