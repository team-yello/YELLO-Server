package com.yello.server.domain.purchase;

import static java.util.Base64.getUrlDecoder;

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

    byte[] kidDecode = getUrlDecoder().decode("OFo0QkRCU1czNQ==");
    byte[] issDecode = getUrlDecoder().decode("MmRlMTMyMGUtY2JlOC00ZDNmLWFmYTctNTc3ZmY0NDM4NzFk");
    byte[] audDecode = getUrlDecoder().decode("YXBwc3RvcmVjb25uZWN0LXYx");
    byte[] bidDecode = getUrlDecoder().decode("WUVMTE8uaU9T");
    byte[] sigDecode = getUrlDecoder().decode(
        "TUlHVEFnRUFNQk1HQnlxR1NNNDlBZ0VHQ0NxR1NNNDlBd0VIQkhrd2R3SUJBUVFnOWpYNkJXbndBVGJiaGxkdw0KVElBL2FkTFptUFJTREYwN01GVjJIb1B2Mkw2Z0NnWUlLb1pJemowREFRZWhSQU5DQUFUVU5oQUJVU1ZLYWhDaw0KbGNINWZscDMveG8xem9PbDV6T0FsWXM2dWIveWM1Qm1HV245c0t2cWtwWHd2c0xZTG5qeUlOZmY4QU1HS2UvcQ0Ka3U3bXNUTVI=");

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
}
