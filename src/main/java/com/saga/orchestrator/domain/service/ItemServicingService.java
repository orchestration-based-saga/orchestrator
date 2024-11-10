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
import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
public class ItemServicingService implements ItemServicingApi {

    private final WorkflowRepositoryApi workflowRepositoryApi;
    private final WorkflowServiceApi workflowServiceApi;

    @Transactional
    @Override
    public void itemServicing(ItemServicingProcess process) {
        if (workflowRepositoryApi.findByBusinessKeyAndProcess(process.getBusinessKey(), process.getProcessId()).isEmpty()) {
            workflowServiceApi.createWorkflow()
                    .map(workflow -> WorkflowProcess.builder()
                            .processId(process.getProcessId())
                            .businessKey(process.getBusinessKey())
                            .workflow(UUID.fromString(workflow.getId()))
                            .state(workflow.getState().getId())
                            .build())
                    .subscribe(workflowProcess -> {
                        var workflow = workflowRepositoryApi.upsert(workflowProcess);
                        process.updateWorkflowProcess(workflow);
                        final var workflowId = workflow.getWorkflow();
                        workflowServiceApi.triggerEvent(
                                        workflow.getWorkflow(),
                                        WorkflowEvent.CREATE_CLAIM,
                                        process)
                                .single()
                                .subscribe(state -> saveState(workflowId, state));
                    });
        }
    }

    @Override
    public void claimCreated(String businessKey, ItemServicingProcess process) {
        Optional<WorkflowProcess> maybeProcess = workflowRepositoryApi
                .findByBusinessKeyAndProcess(businessKey, process.getProcessId());
        if (maybeProcess.isPresent()) {
            WorkflowProcess workflowProcess = maybeProcess.get();
            if (workflowProcess.state.equals(WorkflowState.USER_ACTION_RETURN_TO_WAREHOUSE)) {
                workflowServiceApi.triggerEvent(
                                workflowProcess.getWorkflow(),
                                WorkflowEvent.CLAIM_CREATED,
                                process)
                        .single()
                        .subscribe(state -> saveState(workflowProcess.getWorkflow(), state));
            }
        }
    }

    @Override
    public void shipmentCreated(String businessKey, ItemServicingProcess process) {
        Optional<WorkflowProcess> maybeProcess = workflowRepositoryApi
                .findByBusinessKeyAndProcess(businessKey, process.getProcessId());
        if (maybeProcess.isPresent()) {
            WorkflowProcess workflowProcess = maybeProcess.get();
            if (workflowProcess.state.equals(WorkflowState.CREATE_SHIPMENT)) {
                workflowServiceApi.triggerEvent(
                                workflowProcess.getWorkflow(),
                                WorkflowEvent.SHIPMENT_CREATED,
                                process)
                        .single()
                        .subscribe(state -> saveState(workflowProcess.getWorkflow(), state));
            }
        }
    }

    @Override
    public void courierAssigned(String businessKey, ShipmentProcess process) {
        Optional<WorkflowProcess> maybeProcess = workflowRepositoryApi
                .findByBusinessKeyAndProcess(businessKey, process.getProcessId());
        if (maybeProcess.isPresent()) {
            WorkflowProcess workflowProcess = maybeProcess.get();
            workflowServiceApi.triggerEvent(
                            workflowProcess.getWorkflow(),
                            WorkflowEvent.COURIER_ASSIGNED,
                            process)
                    .single()
                    .subscribe(state -> saveState(workflowProcess.getWorkflow(), state));
        }
    }

    @Override
    public void isPackageDelivered(String businessKey, CheckDeliveryProcess process) {
        Optional<WorkflowProcess> maybeProcess = workflowRepositoryApi
                .findByBusinessKeyAndProcess(businessKey, process.getProcessId());
        if (maybeProcess.isPresent()) {
            WorkflowProcess workflowProcess = maybeProcess.get();
            WorkflowEvent event = process.getIsDelivered() ?
                    WorkflowEvent.PACKAGE_DELIVERED : WorkflowEvent.PACKAGE_NOT_DELIVERED;
            workflowServiceApi.triggerEvent(
                            workflowProcess.getWorkflow(),
                            event,
                            process)
                    .single()
                    .subscribe(state -> saveState(workflowProcess.getWorkflow(), state));
        }
    }

    @Override
    public void warehouseNotified(String businessKey, WarehouseNotified process) {
        Optional<WorkflowProcess> maybeProcess = workflowRepositoryApi
                .findByBusinessKeyAndProcess(businessKey, process.getProcessId());
        if (maybeProcess.isPresent() && process.isSuccess()) {
            WorkflowProcess workflowProcess = maybeProcess.get();
            workflowServiceApi.triggerEvent(
                            workflowProcess.getWorkflow(),
                            WorkflowEvent.WAREHOUSE_NOTIFIED,
                            process)
                    .single()
                    .subscribe(state -> saveState(workflowProcess.getWorkflow(), state));
        } else if (!process.isSuccess()) {
            log.error("Warehouse was not notified successfully. BusinessKey: {}", businessKey);
        }
    }

    @Override
    public void notifiedOfDelivery(String businessKey, String processId) {
        Optional<WorkflowProcess> maybeProcess = workflowRepositoryApi
                .findByBusinessKeyAndProcess(businessKey, processId);
        if (maybeProcess.isPresent()) {
            WorkflowProcess workflowProcess = maybeProcess.get();
            workflowServiceApi.triggerEvent(
                            workflowProcess.getWorkflow(),
                            WorkflowEvent.CLAIM_UPDATED,
                            null)
                    .single()
                    .subscribe(state -> saveState(workflowProcess.getWorkflow(), state));
        }
    }

    @Override
    public void startRefund(ItemRefundProcess process) {
        Optional<WorkflowProcess> maybeProcess = workflowRepositoryApi
                .findParentByBusinessKey(process.getBusinessKey());
        if (maybeProcess.isPresent()) {
            WorkflowProcess workflowProcess = maybeProcess.get();
            workflowServiceApi.triggerEvent(
                            workflowProcess.getWorkflow(),
                            WorkflowEvent.IS_FOR_REFUND_COMPLETED,
                            process)
                    .single()
                    .subscribe(state ->
                            workflowRepositoryApi.createChildProcess(
                                    workflowProcess, process.getBusinessKey(), state, process.getProcessId())
                    );
        }
    }

    private void saveState(UUID workflowId, WorkflowState state) {
        workflowRepositoryApi.updateState(workflowId, state);
    }
}
