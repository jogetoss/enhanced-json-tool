package org.joget.marketplace.workflow;

import org.joget.workflow.model.WorkflowAssignment;
import org.joget.workflow.model.service.WorkflowManager;

import java.util.Map;

public class WorkflowUpdater {
    private final WorkflowManager workflowManager;

    public WorkflowUpdater(WorkflowManager workflowManager) {
        this.workflowManager = workflowManager;
    }

    public void updateResponseStatus(WorkflowAssignment wfAssignment, Map<String, Object> properties, int statusCode) {
        String wfVar = getPropertyString(properties);
        if (!wfVar.isEmpty()) {
            workflowManager.activityVariable(wfAssignment.getActivityId(), wfVar, String.valueOf(statusCode));
        }
    }

    private String getPropertyString(Map<String, Object> properties) {
        Object value = properties.get("responseStatusWorkflowVariable");
        return value != null ? value.toString() : "";
    }
}
