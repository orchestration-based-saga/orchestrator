package com.saga.orchestrator.domain.out;

import com.saga.orchestrator.domain.model.WorkflowProcess;
import com.saga.orchestrator.domain.model.enums.WorkflowState;

import java.util.Optional;
import java.util.UUID;

public interface WorkflowRepositoryApi {

    Optional<WorkflowProcess> findByBusinessKey(String businessKey);

    WorkflowProcess upsert(WorkflowProcess workflowProcess);

    void updateState(UUID workflowId, WorkflowState state);

}
