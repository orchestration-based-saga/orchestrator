package com.saga.orchestrator.application.message.consumer;

import com.saga.orchestrator.application.api.*;
import com.saga.orchestrator.application.exception.BusinessError;
import com.saga.orchestrator.application.mapper.ProcessMapper;
import com.saga.orchestrator.domain.in.ItemServicingApi;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
public class WorkflowConsumer {
    private final ItemServicingApi itemServicingApi;
    private final ProcessMapper mapper;

    @Bean
    public Consumer<Message<WorkflowStartProcessMessage>> startWorkflowProcess() {
        return message -> {
            try {
                WorkflowStartProcessMessage payload = message.getPayload();
                if (payload.processId().equals(WorkflowConstants.ITEM_SERVICING)) {
                    itemServicingApi.itemServicing(mapper.toItemServicingProcess(payload));
                }
            } catch (BusinessError be) {
                processBusinessError(be);
            } catch (RuntimeException e) {
                processTechError(e);
            }
        };
    }

    @Bean
    public Consumer<Message<ItemServicingProcessResponse>> claimCreated() {
        return msg ->
        {
            ItemServicingProcessResponse payload = msg.getPayload();
            itemServicingApi.claimCreated(payload.businessKey(), mapper.fromResponse(payload));
        };
    }

    @Bean
    public Consumer<Message<ItemServicingProcessResponse>> shipmentCreated() {
        return msg ->
        {
            ItemServicingProcessResponse payload = msg.getPayload();
            itemServicingApi.shipmentCreated(payload.businessKey(), mapper.fromResponse(payload));
        };
    }

    @Bean
    public Consumer<Message<ItemServicingProcessShipmentResponse>> courierAssigned() {
        return msg ->
        {
            ItemServicingProcessShipmentResponse payload = msg.getPayload();
            itemServicingApi.courierAssigned(payload.businessKey(), mapper.fromResponse(payload));
        };
    }

    @Bean
    public Consumer<Message<ItemServicingWarehouseNotifiedResponse>> warehouseNotified() {
        return msg -> {
            ItemServicingWarehouseNotifiedResponse payload = msg.getPayload();
            itemServicingApi.warehouseNotified(payload.businessKey(), mapper.fromResponse(payload));
        };
    }

    @Bean
    public Consumer<Message<CheckDeliveryResponse>> checkDelivery() {
        return msg -> {
            CheckDeliveryResponse payload = msg.getPayload();
            itemServicingApi.isPackageDelivered(payload.getBusinessKey(), mapper.fromResponse(payload));
        };
    }

    @Bean
    public Consumer<Message<ItemServicingProcessMessage>> notifiedOfDelivery() {
        return msg -> {
            ItemServicingProcessMessage payload = msg.getPayload();
            itemServicingApi.notifiedOfDelivery(payload.businessKey(), payload.processId());
        };
    }

    @Bean
    public Consumer<Message<ItemRefundProcessRequest>> startRefund() {
        return msg -> {
            ItemRefundProcessRequest payload = msg.getPayload();
            itemServicingApi.startRefund(mapper.fromResponse(payload));
        };
    }

    private void processBusinessError(RuntimeException e) {
    }

    private void processTechError(Exception e) {
    }
}
