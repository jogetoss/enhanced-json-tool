package org.joget.marketplace.auth;

import java.util.Map;

public interface AccessTokenProvider {
    String getAccessToken(Map<String, Object> properties);
}
