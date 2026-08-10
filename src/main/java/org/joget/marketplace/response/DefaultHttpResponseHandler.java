package org.joget.marketplace.response;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.joget.commons.util.LogUtil;
import org.joget.plugin.property.service.PropertyUtil;
import org.joget.workflow.model.WorkflowAssignment;
import org.json.JSONObject;

import java.util.Map;

public class DefaultHttpResponseHandler implements HttpResponseHandler {
    @Override
    public Object handleResponse(HttpResponse response, Map<String, Object> properties, WorkflowAssignment wfAssignment) throws Exception {
        String responseType = getPropertyString(properties, "responseType");
        String jsonResponse = EntityUtils.toString(response.getEntity(), "UTF-8");

        if ("JSON".equalsIgnoreCase(responseType)) {
            String jsonResponseFormatted = formatJsonResponse(jsonResponse);
            LogUtil.info(getClass().getName(), "Formatted JSON Response: " + jsonResponseFormatted);
            JSONObject jsonObj = new JSONObject(jsonResponseFormatted);
            Map jsonResponseObject = PropertyUtil.getProperties(jsonObj);

            if ("true".equalsIgnoreCase(getPropertyString(properties, "enableFormatResponse"))) {
                properties.put("data", jsonResponseObject);
                String script = getPropertyString(properties, "script");
                jsonResponseObject = (Map) executeScript(script, properties);
            }
            // Optionally store to form or workflow variables here
            return jsonResponseObject;
        } else {
            // Handle binary responses (e.g., file download)
            handleBinaryResponse(response, properties, wfAssignment);
            return null;
        }
    }

    private String formatJsonResponse(String jsonResponse) {
        if (jsonResponse == null || jsonResponse.isEmpty()) {
            return "{}";
        }
        if (jsonResponse.startsWith("[") && jsonResponse.endsWith("]")) {
            return "{ \"response\" : " + jsonResponse + " }";
        }
        if (!jsonResponse.startsWith("{") && !jsonResponse.endsWith("}")) {
            return "{ \"response\" : " + jsonResponse + " }";
        }
        return jsonResponse;
    }

    private void handleBinaryResponse(HttpResponse response, Map<String, Object> properties, WorkflowAssignment wfAssignment) {
        // Implement binary response handling (extract filename, save file, update form, etc.)
    }

    private Object executeScript(String script, Map<String, Object> properties) {
        // Execute formatting script logic (e.g., via BeanShell)
        return null;
    }

    private String getPropertyString(Map<String, Object> properties, String key) {
        Object value = properties.get(key);
        return value != null ? value.toString() : "";
    }
}

