package org.joget.marketplace.http;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.SystemDefaultRoutePlanner;
import org.apache.http.HttpResponse;

import java.io.IOException;
import java.net.ProxySelector;

public class HttpClientHandler {
    private final int connectionTimeout;
    private final int socketTimeout;

    public HttpClientHandler(int connectionTimeout, int socketTimeout) {
        this.connectionTimeout = connectionTimeout;
        this.socketTimeout = socketTimeout;
    }

    public CloseableHttpClient createClient() {
        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(connectionTimeout)
                .setSocketTimeout(socketTimeout)
                .build();

        return HttpClients.custom()
                .setDefaultRequestConfig(config)
                .setRoutePlanner(new SystemDefaultRoutePlanner(ProxySelector.getDefault()))
                .build();
    }

    public HttpResponse executeRequest(CloseableHttpClient client, HttpRequestBase request) throws IOException {
        return client.execute(request);
    }
}
