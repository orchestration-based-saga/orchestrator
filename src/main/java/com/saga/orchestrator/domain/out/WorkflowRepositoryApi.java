package com.saga.orchestrator.domain.out;

import com.saga.orchestrator.domain.model.WorkflowProcess;

import java.util.Optional;

public interface WorkflowRepositoryApi {

    Optional<WorkflowProcess> findByProcessId(String processId);

    void upsert(WorkflowProcess workflowProcess);
}
