package org.joget.marketplace.auth;

import org.joget.marketplace.TokenApiUtil;

import java.util.Map;

public class TokenApiAccessTokenProvider implements AccessTokenProvider {
    @Override
    public String getAccessToken(Map<String, Object> properties) {
        // If the "accessToken" property is true, retrieve it using TokenApiUtil
        if ("true".equalsIgnoreCase((String) properties.get("accessToken"))) {
            return new TokenApiUtil().getToken(properties);
        }
        return "";
    }
}
