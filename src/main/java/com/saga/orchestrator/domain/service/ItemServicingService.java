package com.saga.orchestrator.domain.service;

import com.saga.orchestrator.domain.in.ItemServicingApi;
import com.saga.orchestrator.domain.model.*;
import com.saga.orchestrator.domain.model.enums.WorkflowEvent;
import com.saga.orchestrator.domain.model.enums.WorkflowState;
import com.saga.orchestrator.domain.out.WorkflowRepositoryApi;
import com.saga.orchestrator.domain.out.WorkflowServiceApi;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
public class ItemServicingService implements ItemServicingApi {

    private final WorkflowRepositoryApi workflowRepositoryApi;
    private final WorkflowServiceApi workflowServiceApi;

    @Transactional
    @Override
    public void itemServicing(ItemServicingProcess process) {
        if (workflowRepositoryApi.findByBusinessKey(process.getBusinessKey()).isEmpty()) {
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
            saveState(workflowProcess, workflow.getCurrentState());
        }
    }

    @Override
    public void claimCreated(String businessKey, ItemServicingProcess process) {
        Optional<WorkflowProcess> maybeProcess = workflowRepositoryApi.findByBusinessKey(businessKey);
        if (maybeProcess.isPresent()) {
            WorkflowProcess workflowProcess = maybeProcess.get();
            if (workflowProcess.state.equals(WorkflowState.USER_ACTION_RETURN_TO_WAREHOUSE)) {
                StateMachineInstance stateMachine = workflowServiceApi.triggerEvent(
                        workflowProcess.getWorkflow(),
                        WorkflowEvent.CLAIM_CREATED,
                        process);
                saveState(workflowProcess, stateMachine.getCurrentState());
            }
        }
    }

    @Override
    public void shipmentCreated(String businessKey, ItemServicingProcess process) {
        Optional<WorkflowProcess> maybeProcess = workflowRepositoryApi.findByBusinessKey(businessKey);
        if (maybeProcess.isPresent()) {
            WorkflowProcess workflowProcess = maybeProcess.get();
            if (workflowProcess.state.equals(WorkflowState.CREATE_SHIPMENT)) {
                StateMachineInstance stateMachine = workflowServiceApi.triggerEvent(
                        workflowProcess.getWorkflow(),
                        WorkflowEvent.SHIPMENT_CREATED,
                        process);
                saveState(workflowProcess, stateMachine.getCurrentState());
            }
        }
    }

    @Override
    public void courierAssigned(String businessKey, ShipmentProcess process) {
        Optional<WorkflowProcess> maybeProcess = workflowRepositoryApi.findByBusinessKey(businessKey);
        if (maybeProcess.isPresent()) {
            WorkflowProcess workflowProcess = maybeProcess.get();
            StateMachineInstance stateMachine = workflowServiceApi.triggerEvent(
                    workflowProcess.getWorkflow(),
                    WorkflowEvent.COURIER_ASSIGNED,
                    process);
            saveState(workflowProcess, stateMachine.getCurrentState());
        }
    }

    @Override
    public void isPackageDelivered(String businessKey, CheckDeliveryProcess process) {
        Optional<WorkflowProcess> maybeProcess = workflowRepositoryApi.findByBusinessKey(businessKey);
        if (maybeProcess.isPresent()) {
            WorkflowProcess workflowProcess = maybeProcess.get();
            WorkflowEvent event = process.getIsDelivered() ?
                    WorkflowEvent.PACKAGE_DELIVERED : WorkflowEvent.PACKAGE_NOT_DELIVERED;
            StateMachineInstance stateMachine = workflowServiceApi.triggerEvent(
                    workflowProcess.getWorkflow(),
                    event,
                    process);
            saveState(workflowProcess, stateMachine.getCurrentState());
        }
    }

    private void saveState(WorkflowProcess workflowProcess, WorkflowState state) {
        workflowRepositoryApi.updateState(workflowProcess.getWorkflow(), state);
    }
}
