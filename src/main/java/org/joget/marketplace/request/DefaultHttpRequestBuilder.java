package org.joget.marketplace.request;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.joget.commons.util.LogUtil;
import org.joget.workflow.model.WorkflowAssignment;
import org.joget.workflow.util.WorkflowUtil;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class DefaultHttpRequestBuilder implements HttpRequestBuilder {
    @Override
    public HttpRequestBase buildRequest(String jsonUrl, Map<String, Object> properties, WorkflowAssignment wfAssignment, String accessToken) throws Exception {
        String requestType = getPropertyString(properties, "requestType");
        HttpRequestBase request;
        if ("post".equalsIgnoreCase(requestType)) {
            request = buildPostRequest(jsonUrl, properties, wfAssignment);
        } else if ("put".equalsIgnoreCase(requestType)) {
            request = buildPutRequest(jsonUrl, properties, wfAssignment);
        } else if ("patch".equalsIgnoreCase(requestType)) {
            request = buildPatchRequest(jsonUrl, properties, wfAssignment);
        } else {
            request = new HttpGet(jsonUrl);
        }
        addHeaders(request, properties, accessToken);
        return request;
    }

    private HttpPost buildPostRequest(String url, Map<String, Object> properties, WorkflowAssignment wfAssignment) throws Exception {
        HttpPost post = new HttpPost(url);
        String postMethod = getPropertyString(properties, "postMethod");
        if ("jsonPayload".equalsIgnoreCase(postMethod)) {
            JSONObject obj = new JSONObject();
            Object[] paramsValues = (Object[]) properties.get("params");
            for (Object o : paramsValues) {
                Map mapping = (HashMap) o;
                String name = mapping.get("name").toString();
                String value = WorkflowUtil.processVariable(mapping.get("value").toString(), "", wfAssignment);
                obj.accumulate(name, value);
            }
            StringEntity requestEntity = new StringEntity(obj.toString(4), "UTF-8");
            post.setEntity(requestEntity);
            post.setHeader("Content-type", "application/json");
            LogUtil.info(getClass().getName(), "JSON Payload : " + obj.toString(4));
        } else if ("custom".equalsIgnoreCase(postMethod)) {
            String customPayload = getPropertyString(properties, "customPayload");
            StringEntity requestEntity = new StringEntity(customPayload, "UTF-8");
            post.setEntity(requestEntity);
            post.setHeader("Content-type", "application/json");
            LogUtil.info(getClass().getName(), "Custom JSON Payload : " + customPayload);
        } else {
            // Build multipart payload (parameters and attachments)
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            // (Add parameters and file attachments in dedicated helper methods.)
            HttpEntity entity = builder.build();
            post.setEntity(entity);
        }
        return post;
    }

    private HttpPut buildPutRequest(String url, Map<String, Object> properties, WorkflowAssignment wfAssignment) throws Exception {
        HttpPut put = new HttpPut(url);
        // Similar logic as buildPostRequest…
        return put;
    }

    private HttpPatch buildPatchRequest(String url, Map<String, Object> properties, WorkflowAssignment wfAssignment) throws Exception {
        HttpPatch patch = new HttpPatch(url);
        // Similar logic as buildPostRequest…
        return patch;
    }

    private void addHeaders(HttpRequestBase request, Map<String, Object> properties, String accessToken) {
        Object[] headerValues = (Object[]) properties.get("headers");
        for (Object o : headerValues) {
            Map mapping = (HashMap) o;
            String name = mapping.get("name").toString();
            String value = mapping.get("value").toString();
            if (value.contains("{accessToken}")) {
                value = value.replace("{accessToken}", accessToken);
            }
            request.setHeader(name, value);
            LogUtil.info(getClass().getName(), "Adding request header " + name + " : " + value);
        }
    }

    private String getPropertyString(Map<String, Object> properties, String key) {
        Object value = properties.get(key);
        return value != null ? value.toString() : "";
    }
}

