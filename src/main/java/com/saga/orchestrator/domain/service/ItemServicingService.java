package com.saga.orchestrator.domain.service;

import com.saga.orchestrator.domain.in.ItemServicingApi;
import com.saga.orchestrator.domain.model.ItemServicingProcess;
import com.saga.orchestrator.domain.model.StateMachineInstance;
import com.saga.orchestrator.domain.model.WorkflowProcess;
import com.saga.orchestrator.domain.model.enums.WorkflowEvent;
import com.saga.orchestrator.domain.out.WorkflowRepositoryApi;
import com.saga.orchestrator.domain.out.WorkflowServiceApi;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
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

            workflowProcess = workflowRepositoryApi.upsert(workflowProcess);
            process.updateWorkflowProcess(workflowProcess);
            workflowServiceApi.triggerEvent(
                    workflowProcess.getWorkflow(),
                    WorkflowEvent.CREATE_CLAIM,
                    process);
            saveState(workflowProcess.getWorkflow());
        }
    }

    @Override
    public void claimCreated(UUID workflowId) {
        workflowServiceApi.triggerEvent(
                workflowId,
                WorkflowEvent.CLAIM_CREATED,
                null);
        saveState(workflowId);
    }

    private void saveState(UUID workflowId) {
        WorkflowProcess workflowProcess = workflowRepositoryApi.findByWorkflowId(workflowId);
        if (workflowProcess == null) {
            log.error("State transition happened but workflow not found");
            // todo throw error
            return;
        }
        StateMachineInstance stateMachine = workflowServiceApi.getWorkflow(workflowProcess.getWorkflow());
        workflowRepositoryApi.updateState(workflowProcess.getWorkflow(), stateMachine.getCurrentState());
    }
}
