package org.joget.marketplace.request;

import org.apache.http.client.methods.HttpRequestBase;
import org.joget.workflow.model.WorkflowAssignment;

import java.util.Map;

public interface HttpRequestBuilder {
    HttpRequestBase buildRequest(String jsonUrl, Map<String, Object> properties, WorkflowAssignment wfAssignment, String accessToken) throws Exception;
}

