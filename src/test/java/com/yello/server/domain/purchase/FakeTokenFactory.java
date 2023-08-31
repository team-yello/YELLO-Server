package com.yello.server.domain.purchase;

import static java.util.Base64.getUrlDecoder;

import com.yello.server.domain.purchase.dto.response.AppleJwsTransactionResponse;
import com.yello.server.global.common.factory.TokenFactory;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Date;
import org.apache.commons.codec.binary.Base64;

public class FakeTokenFactory implements TokenFactory {

    byte[] kidDecode = getUrlDecoder().decode("UlpBM1c0MlZRNQ==");
    byte[] issDecode = getUrlDecoder().decode("MmRlMTMyMGUtY2JlOC00ZDNmLWFmYTctNTc3ZmY0NDM4NzFk");
    byte[] audDecode = getUrlDecoder().decode("YXBwc3RvcmVjb25uZWN0LXYx");
    byte[] bidDecode = getUrlDecoder().decode("WUVMTE8uaU9T");
    byte[] sigDecode = getUrlDecoder().decode(
        "TUlHVEFnRUFNQk1HQnlxR1NNNDlBZ0VHQ0NxR1NNNDlBd0VIQkhrd2R3SUJBUVFncVNuWnI2TGlic01Fa2QwYQ0KeFJqdG5VaXhReVNGdnZOb2RvRklrNEdmUlFLZ0NnWUlLb1pJemowREFRZWhSQU5DQUFRekcvN3NQYWM0bHFxWg0KMWYrNnRQVHBRZWNHOEtxV2hZRktnQ1NiaFhmb2dvTzJ1eFhuNyttbWR3NzdkYmVVZ0tDdkxTdlM1R2ZwU1NrMw0KeEZGNFh3Y3E=");

    private String kid = new String(kidDecode);
    private String iss = new String(issDecode);
    private String aud = new String(audDecode);
    private String bid = new String(bidDecode);
    private String sig = new String(sigDecode);


    @Override
    public String generateAppleToken() {
        try {
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
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public AppleJwsTransactionResponse decodeTransactionToken(String signedTransactionInfo) {
        return null;
    }
}
