package org.joget.marketplace.response;

import org.apache.http.HttpResponse;
import org.joget.workflow.model.WorkflowAssignment;

import java.util.Map;

public interface HttpResponseHandler {
    Object handleResponse(HttpResponse response, Map<String, Object> properties, WorkflowAssignment wfAssignment) throws Exception;
}
