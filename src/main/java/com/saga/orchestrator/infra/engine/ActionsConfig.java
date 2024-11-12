package com.saga.orchestrator.infra.engine;

import com.saga.orchestrator.domain.in.ItemServicingActionApi;
import com.saga.orchestrator.domain.model.*;
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
                context.getExtendedState().getVariables().put("packageId", process.getShipment().getPackageId());
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
            log.info("Checking status of delivery of package {}, businessKey {}", packageId, businessKey);
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
            if (data instanceof ItemServicingProcess process) {
                itemServicingActionApi.createShipment(process, workflowId);
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
            if (data instanceof ItemServicingProcess process) {
                itemServicingActionApi.createShipment(process, workflowId);
            }
        };
    }

    @Bean
    public Action<WorkflowState, WorkflowEvent> notifyOfDeliveredPackage() {
        return context -> {
            Object data = context.getMessageHeader("data");
            UUID workflowId = (UUID) context.getMessageHeader("workflowId");
            if (data == null) {
                log.error("Can't notify about package delivery");
                // todo throw an error
            }
            if (data instanceof CheckDeliveryProcess process) {
                itemServicingActionApi.notifyOfDeliveredPackage(process, workflowId);
            }
        };
    }

    @Bean
    public Action<WorkflowState, WorkflowEvent> initiateRefund() {
        return context -> {
            Object data = context.getMessageHeader("data");
            UUID workflowId = (UUID) context.getMessageHeader("workflowId");
            if (data == null) {
                log.error("Can't initiate refund");
                // todo throw an error
            }
            if (data instanceof ItemRefundProcess process) {
                context.getExtendedState().getVariables().put("orderId", process.getClaim().getOrderId());
                context.getExtendedState().getVariables().put("workflowId", workflowId);
                context.getExtendedState().getVariables().put("processId", process.getProcessId());
            }
        };
    }

    @Bean
    public Action<WorkflowState, WorkflowEvent> checkIfRefunded() {
        return context -> {
            String orderId = (String) context.getExtendedState().getVariables().get("orderId");
            String businessKey = (String) context.getExtendedState().getVariables().get("businessKey");
            UUID workflowId = (UUID) context.getExtendedState().getVariables().get("workflowId");
            String processId = (String) context.getExtendedState().getVariables().get("processId");
            itemServicingActionApi.checkIfRefundCompleted(processId, orderId, workflowId, businessKey);
            log.info("Checking status of refund of order {}, businessKey {}", orderId, businessKey);

        };
    }
}
