package com.saga.orchestrator.domain.out;

import com.saga.orchestrator.domain.model.StateMachineInstance;
import com.saga.orchestrator.domain.model.enums.WorkflowEvent;

import java.util.UUID;

public interface WorkflowServiceApi {

    StateMachineInstance createWorkflow();

    StateMachineInstance triggerEvent(UUID workflowId, WorkflowEvent event, Object data);
}
