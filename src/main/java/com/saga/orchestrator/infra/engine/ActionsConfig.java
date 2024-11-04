package com.saga.orchestrator.infra.engine;

import com.saga.orchestrator.domain.in.ItemServicingActionApi;
import com.saga.orchestrator.domain.model.ItemServicingProcess;
import com.saga.orchestrator.domain.model.ShipmentProcess;
import com.saga.orchestrator.domain.model.enums.WorkflowEvent;
import com.saga.orchestrator.domain.model.enums.WorkflowState;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class ActionsConfig {

    private final ItemServicingActionApi itemServicingActionApi;

    @Bean
    Action<WorkflowState, WorkflowEvent> notifyWarehouseOfIncomingDelivery() {
        return context -> {
            Object data = context.getMessageHeader("data");
            UUID workflowId = (UUID) context.getMessageHeader("workflowId");
            if (data == null) {
                log.error("Can't notify warehouse of incoming package");
                // todo throw an error
            }
            if (data instanceof ShipmentProcess process) {
                context.getExtendedState().getVariables().put("packageId", process.getShipment().packageId());
                context.getExtendedState().getVariables().put("businessKey", process.getBusinessKey());
                itemServicingActionApi.notifyWarehouse(process, workflowId);
            }
        };
    }

    @Bean
    Action<WorkflowState, WorkflowEvent> checkIfDelivered() {
        return context -> {
            String packageId = (String) context.getExtendedState().getVariables().get("packageId");
            String businessKey = (String) context.getExtendedState().getVariables().get("businessKey");
            UUID workflowId = (UUID) context.getMessageHeader("workflowId");
            itemServicingActionApi.checkIfPackageIsDelivered(businessKey, packageId, workflowId);
        };
    }

    @Bean
    public Action<WorkflowState, WorkflowEvent> addRequestToContext() {
        return context -> {
            Object data = context.getMessageHeader("data");
            if (data == null) {
                log.error("Can't save assign courier request");
                // todo throw an error
            }
            if (data instanceof ItemServicingProcess) {
                context.getExtendedState().getVariables()
                        .getOrDefault("assignCourierRequest", null);
                context.getExtendedState().getVariables().put("assignCourierRequest", data);
            }
        };
    }


    @Bean
    public Action<WorkflowState, WorkflowEvent> createClaim() {
        return context -> {
            Object data = context.getMessageHeader("data");
            UUID workflowId = (UUID) context.getMessageHeader("workflowId");
            if (data == null) {
                log.error("Can't create claim");
                // todo throw an error
            }
            if (data instanceof ItemServicingProcess itemServicingProcess) {
                itemServicingActionApi.createClaim(itemServicingProcess, workflowId);
            }
        };
    }

    @Bean
    public Action<WorkflowState, WorkflowEvent> updateClaim() {
        return context -> {
            Object data = context.getMessageHeader("data");
            UUID workflowId = (UUID) context.getMessageHeader("workflowId");
            if (data == null) {
                log.error("Can't update claim");
                // todo throw an error
            }
            if (data instanceof ItemServicingProcess itemServicingProcess) {
                itemServicingActionApi.updateClaim(itemServicingProcess, workflowId);
            }
        };
    }

    @Bean
    public Action<WorkflowState, WorkflowEvent> assignCourierToShipment() {
        return context -> {
            Object data = context.getMessageHeader("data");
            UUID workflowId = (UUID) context.getMessageHeader("workflowId");
            if (data == null) {
                log.error("Can't assign courier");
                // todo throw an error
            }
            if (data instanceof ItemServicingProcess itemServicingProcess) {
                itemServicingActionApi.assignCourier(itemServicingProcess, workflowId);
            }
        };
    }

    @Bean
    public Action<WorkflowState, WorkflowEvent> reassignCourier() {
        return context -> {
            Object data = context.getExtendedState().getVariables()
                    .get("assignCourierRequest");
            UUID workflowId = (UUID) context.getMessageHeader("workflowId");
            if (data == null) {
                log.error("Can't reassign courier");
                // todo throw an error
            }
            if (data instanceof ItemServicingProcess itemServicingProcess) {
                itemServicingActionApi.assignCourier(itemServicingProcess, workflowId);
            }
        };
    }

    @Bean
    public Action<WorkflowState, WorkflowEvent> createShipment() {
        return context -> {
            Object data = context.getMessageHeader("data");
            UUID workflowId = (UUID) context.getMessageHeader("workflowId");
            if (data == null) {
                log.error("Can't create shipment");
                // todo throw an error
            }
            if (data instanceof ItemServicingProcess) {
                itemServicingActionApi.createShipment((ItemServicingProcess) data, workflowId);
            }
        };
    }

    @Bean
    public Action<WorkflowState, WorkflowEvent> updateShipment() {
        return context -> {
            Object data = context.getMessageHeader("data");
            UUID workflowId = (UUID) context.getMessageHeader("workflowId");
            if (data == null) {
                log.error("Can't create shipment");
                // todo throw an error
            }
            if (data instanceof ItemServicingProcess) {
                itemServicingActionApi.createShipment((ItemServicingProcess) data, workflowId);
            }
        };
    }
}
