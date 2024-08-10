package com.saga.orchestrator.domain.out;

import com.saga.orchestrator.domain.model.StateMachineInstance;
import com.saga.orchestrator.domain.model.enums.WorkflowEvent;

import java.util.UUID;

public interface WorkflowServiceApi {

    StateMachineInstance createWorkflow();

    void triggerEvent(UUID workflowId, WorkflowEvent event, Object data);

    StateMachineInstance getWorkflow(UUID workflowId);
}
