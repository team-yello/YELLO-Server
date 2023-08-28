package com.yello.server.domain.purchase;

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

    private String kid = "8Z4BDBSW35";

    private String iss = "2de1320e-cbe8-4d3f-afa7-577ff443871d";

    private String aud = "appstoreconnect-v1";

    private String bid = "YELLO.iOS";

    private String sig = "MIGTAgEAMBMGByqGSM49AgEGCCqGSM49AwEHBHkwdwIBAQQg9jX6BWnwATbbhldw\n"
        + "TIA/adLZmPRSDF07MFV2HoPv2L6gCgYIKoZIzj0DAQehRANCAATUNhABUSVKahCk\n"
        + "lcH5flp3/xo1zoOl5zOAlYs6ub/yc5BmGWn9sKvqkpXwvsLYLnjyINff8AMGKe/q\n"
        + "ku7msTMR";

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
}
