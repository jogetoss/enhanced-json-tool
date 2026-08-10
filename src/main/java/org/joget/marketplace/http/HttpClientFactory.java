package org.joget.marketplace.http;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.SystemDefaultRoutePlanner;
import org.joget.marketplace.config.TimeoutConfig;

import java.net.ProxySelector;

public class HttpClientFactory {
    public CloseableHttpClient createHttpClient(TimeoutConfig timeoutConfig) {
        return HttpClients.custom()
                .setDefaultRequestConfig(timeoutConfig.toRequestConfig())
                .setRoutePlanner(new SystemDefaultRoutePlanner(ProxySelector.getDefault()))
                .build();
    }
}

