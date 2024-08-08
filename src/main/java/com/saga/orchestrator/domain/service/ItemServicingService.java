package com.saga.orchestrator.domain.service;

import com.saga.orchestrator.domain.in.ItemServicingApi;
import com.saga.orchestrator.domain.model.ItemServicingProcess;
import com.saga.orchestrator.domain.model.StateMachineInstance;
import com.saga.orchestrator.domain.model.WorkflowProcess;
import com.saga.orchestrator.domain.out.WorkflowRepositoryApi;
import com.saga.orchestrator.domain.out.WorkflowServiceApi;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ItemServicingService implements ItemServicingApi {

    private final WorkflowRepositoryApi workflowRepositoryApi;
    private final WorkflowServiceApi workflowServiceApi;

    @Override
    public void itemServicing(ItemServicingProcess process) {
        if (workflowRepositoryApi.findByProcessId(process.getProcessId()).isEmpty()) {
            StateMachineInstance workflow = workflowServiceApi.createWorkflow();
            WorkflowProcess workflowProcess = WorkflowProcess.builder()
                    .processId(process.getProcessId())
                    .businessKey(process.getBusinessKey())
                    .workflow(workflow.getId())
                    .state(workflow.getCurrentState())
                    .build();

            workflowRepositoryApi.upsert(workflowProcess);
        }
    }
}
