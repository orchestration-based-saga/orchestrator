package com.saga.orchestrator.domain.out;

import com.saga.orchestrator.domain.model.WorkflowProcess;
import com.saga.orchestrator.domain.model.enums.WorkflowState;

import java.util.Optional;
import java.util.UUID;

public interface WorkflowRepositoryApi {

    Optional<WorkflowProcess> findByBusinessKeyAndProcess(String businessKey, String processId);

    WorkflowProcess upsert(WorkflowProcess workflowProcess);

    void updateState(UUID workflowId, WorkflowState state);

    WorkflowProcess createChildProcess(WorkflowProcess parentProcess, String businessKey, WorkflowState state, String processId);

    Optional<WorkflowProcess> findParentByBusinessKey(String businessKey);
}
