package org.joget.marketplace.config;

import org.apache.http.client.config.RequestConfig;
import org.joget.commons.util.LogUtil;

public class TimeoutConfig {
    private final int connectionTimeout;
    private final int socketTimeout;

    public TimeoutConfig(String connectionTimeoutStr, String socketTimeoutStr) {
        int defaultTimeout = 30000;
        int connTimeout = defaultTimeout;
        int sockTimeout = defaultTimeout;
        try {
            if (connectionTimeoutStr != null && !connectionTimeoutStr.isEmpty()) {
                connTimeout = Integer.parseInt(connectionTimeoutStr) * 1000;
            }
            if (socketTimeoutStr != null && !socketTimeoutStr.isEmpty()) {
                sockTimeout = Integer.parseInt(socketTimeoutStr) * 1000;
            }
        } catch (NumberFormatException e) {
            LogUtil.warn(getClass().getName(), "Invalid timeout value provided. Using default timeouts.");
        }
        this.connectionTimeout = connTimeout;
        this.socketTimeout = sockTimeout;
        LogUtil.info(getClass().getName(), "Connection Timeout set to: " + connectionTimeout + " ms");
        LogUtil.info(getClass().getName(), "Socket Timeout set to: " + socketTimeout + " ms");
    }

    public RequestConfig toRequestConfig() {
        return RequestConfig.custom()
                .setConnectTimeout(connectionTimeout)
                .setSocketTimeout(socketTimeout)
                .build();
    }
}

