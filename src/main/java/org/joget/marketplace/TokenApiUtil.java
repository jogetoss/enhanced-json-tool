package org.joget.marketplace;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.Map;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.joget.commons.util.LogUtil;
import java.net.ProxySelector;
import org.apache.http.impl.conn.SystemDefaultRoutePlanner;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class TokenApiUtil {

    public String getToken(Map properties) {
        String accessToken = "";
        String tokenUrl = (String) properties.get("tokenUrl");
        String tokenRequestType = (String) properties.get("tokenRequestType");
        String tokenJsonBoy = (String) properties.get("tokenJsonBoy");
        String tokenFieldName = (String) properties.get("tokenFieldName");
        CloseableHttpClient client = null;
        HttpRequestBase tokenRequest = null;
        if ("post".equalsIgnoreCase(tokenRequestType)) {
            try {
                client = HttpClients.custom().setRoutePlanner(new SystemDefaultRoutePlanner(ProxySelector.getDefault())).build();
                tokenRequest = new HttpPost(tokenUrl);
                StringEntity entity;
                tokenRequest = new HttpPost(tokenUrl);
                entity = new StringEntity(tokenJsonBoy);
                ((HttpPost) tokenRequest).setEntity(entity);
                tokenRequest.setHeader("Accept", "application/json");
                tokenRequest.setHeader("Content-Type", "application/json");
                HttpResponse response = client.execute(tokenRequest);
                if (response.getStatusLine().getStatusCode() >= 200 && response.getStatusLine().getStatusCode() <= 300) {
                    String jsonResponse = EntityUtils.toString(response.getEntity(), "UTF-8");
                    if (jsonResponse != null && !jsonResponse.isEmpty()) {
                        accessToken = getFieldValueFromResponse(jsonResponse, tokenFieldName);
                    }
                }
            } catch (UnsupportedEncodingException ex) {
                LogUtil.error(getClassName(), ex, ex.getMessage());
            } catch (IOException ex) {
                LogUtil.error(getClassName(), ex, ex.getMessage());
            } finally {
                try {
                    if (tokenRequest != null) {
                        tokenRequest.releaseConnection();
                    }
                    if (client != null) {
                        client.close();
                    }
                } catch (IOException ex) {
                    LogUtil.error(getClass().getName(), ex, "");
                }
            }
        } else {
            try {
                client = HttpClients.custom().setRoutePlanner(new SystemDefaultRoutePlanner(ProxySelector.getDefault())).build();
                tokenRequest = new HttpGet(tokenUrl);
                HttpResponse response = client.execute(tokenRequest);
                if (response.getStatusLine().getStatusCode() >= 200 && response.getStatusLine().getStatusCode() <= 300) {
                    String jsonResponse = EntityUtils.toString(response.getEntity(), "UTF-8");
                    if (jsonResponse != null && !jsonResponse.isEmpty()) {
                        accessToken = getFieldValueFromResponse(jsonResponse, tokenFieldName);
                    }
                }
            } catch (IOException ex) {
                LogUtil.error(EnhancedJsonTool.class.getName(), ex, ex.getMessage());
            } finally {
                try {
                    if (tokenRequest != null) {
                        tokenRequest.releaseConnection();
                    }
                    if (client != null) {
                        client.close();
                    }
                } catch (IOException ex) {
                    LogUtil.error(getClass().getName(), ex, "");
                }
            }
        }
        return accessToken;
    }

    private String getFieldValueFromResponse(String jsonResponse, String fieldName) {
        try {
            JSONTokener tokener = new JSONTokener(jsonResponse);
            Object json = tokener.nextValue();

            if (json instanceof JSONObject) {
                JSONObject jsonObject = (JSONObject) json;
                return getFieldFromObject(jsonObject, fieldName);
            } else if (json instanceof JSONArray) {
                JSONArray jsonArray = (JSONArray) json;
                return getFieldFromArray(jsonArray, fieldName);
            } else {
                LogUtil.info(getClassName(), "Invalid JSON response format.");
                return null;
            }
        } catch (JSONException ex) {
            LogUtil.error(EnhancedJsonTool.class.getName(), ex, ex.getMessage());
            return null;
        }
    }

    private String getClassName() {
        return this.getClass().getName();
    }

    private String getFieldFromObject(JSONObject jsonObject, String fieldName) {
        if (jsonObject.has(fieldName)) {
            try {
                return jsonObject.getString(fieldName);
            } catch (JSONException ex) {
                LogUtil.error(getClassName(), ex, ex.getMessage());
            }
        } else {
            Iterator<String> keys = jsonObject.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                try {
                    Object value = jsonObject.get(key);
                    if (value instanceof JSONObject) {
                        String result = getFieldFromObject((JSONObject) value, fieldName);
                        if (result != null) {
                            return result;
                        }
                    } else if (value instanceof JSONArray) {
                        String result = getFieldFromArray((JSONArray) value, fieldName);
                        if (result != null) {
                            return result;
                        }
                    }
                } catch (JSONException ex) {
                    LogUtil.error(getClassName(), ex, ex.getMessage());
                }

            }
        }
        LogUtil.info(getClassName(), "Field " + fieldName + " not found in JSON object.");
        return null;
    }

    private String getFieldFromArray(JSONArray jsonArray, String fieldName) {
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String result = getFieldFromObject(jsonObject, fieldName);
                if (result != null) {
                    return result;
                }
            } catch (JSONException ex) {
                LogUtil.error(getClassName(), ex, ex.getMessage());
            }
        }
        LogUtil.info(getClassName(), "Field " + fieldName + " in any JSON object within the array.");
        return null;
    }

}
