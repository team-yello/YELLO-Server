package com.yello.server.global.common.factory;

import java.util.Map;
import org.springframework.boot.json.BasicJsonParser;

public class DecodeTokenFactory {

    public static Map<String, Object> decodeToken(String jwtToken) {
        final String payloadJWT = jwtToken.split("\\.")[1];

        final String payload = new String(java.util.Base64.getUrlDecoder().decode(payloadJWT));
        BasicJsonParser jsonParser = new BasicJsonParser();
        Map<String, Object> jsonArray = jsonParser.parseMap(payload);
        
        return jsonArray;
    }

}
