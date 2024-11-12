package com.saga.orchestrator.domain.service;

import com.saga.orchestrator.application.api.WorkflowConstants;
import com.saga.orchestrator.domain.in.ItemServicingActionApi;
import com.saga.orchestrator.domain.model.*;
import com.saga.orchestrator.domain.out.WorkflowProducerApi;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
public class ItemServicingActions implements ItemServicingActionApi {

    private final WorkflowProducerApi workflowProducerApi;

    @Override
    public void createClaim(ItemServicingProcess process, UUID workflowId) {
        workflowProducerApi.sendServiceTaskRequest(TopicUtils.CREATE_CLAIM_TOPIC, process);
    }

    @Override
    public void updateClaim(ItemServicingProcess process, UUID workflowId) {
        workflowProducerApi.sendServiceTaskRequest(TopicUtils.UPDATE_CLAIM_TOPIC, process);
    }

    @Override
    public void createShipment(ItemServicingProcess process, UUID workflowId) {
        workflowProducerApi.sendServiceTaskRequest(TopicUtils.CREATE_SHIPMENT_TOPIC, process);
    }

    @Override
    public void assignCourier(ItemServicingProcess process, UUID workflowId) {
        workflowProducerApi.sendServiceTaskRequest(TopicUtils.ASSIGN_COURIER_TOPIC, process);
    }

    @Override
    public void notifyWarehouse(ShipmentProcess process, UUID workflowId) {
        workflowProducerApi.sendServiceTaskRequest(TopicUtils.NOTIFY_WAREHOUSE, process);
    }

    @Override
    public void checkIfPackageIsDelivered(String businessKey, String packageId, UUID workflowId) {
        CheckDeliveryProcess process = CheckDeliveryProcess.builder()
                .businessKey(businessKey)
                .processId(WorkflowConstants.ITEM_SERVICING)
                .packageId(packageId)
                .build();
        workflowProducerApi.sendServiceTaskRequest(TopicUtils.CHECK_DELIVERY, process);
    }

    @Override
    public void notifyOfDeliveredPackage(CheckDeliveryProcess process, UUID workflowId) {
        DeliveredPackageNotification msg = DeliveredPackageNotification.builder()
                .businessKey(process.getBusinessKey())
                .processId(WorkflowConstants.ITEM_SERVICING)
                .packageId(process.getPackageId())
                .build();
        workflowProducerApi.sendServiceTaskRequest(TopicUtils.DELIVERED_PACKAGE, msg);
    }

    @Override
    public void checkIfRefundCompleted(String processId, String orderId, UUID workflowId, String businessKey) {
        var process = CheckRefundProcess.builder()
                .businessKey(businessKey)
                .workflow(workflowId)
                .orderId(orderId)
                .build();
        workflowProducerApi.sendServiceTaskRequest(TopicUtils.CHECK_REFUND, process);
    }
}
