package com.saga.orchestrator.application.message.producer;


import com.saga.orchestrator.application.api.*;
import com.saga.orchestrator.application.mapper.ProcessMapper;
import com.saga.orchestrator.domain.model.*;
import com.saga.orchestrator.domain.out.WorkflowProducerApi;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WorkflowProducer implements WorkflowProducerApi {

    private final StreamBridge streamBridge;
    private final ProcessMapper mapper;

    @Override
    public void sendServiceTaskRequest(String topic, ItemServicingProcess data) {
        ItemServicingProcessMessage message = mapper.toMessage(data);
        streamBridge.send(topic, message);
    }

    @Override
    public void sendServiceTaskRequest(String topic, ShipmentProcess data) {
        ShipmentProcessMessage message = mapper.toMessage(data);
        streamBridge.send(topic, message);
    }

    @Override
    public void sendServiceTaskRequest(String topic, CheckDeliveryProcess data) {
        CheckDeliveryProcessMessage message = mapper.toMessage(data);
        streamBridge.send(topic, message);
    }

    @Override
    public void sendServiceTaskRequest(String topic, DeliveredPackageNotification data) {
        DeliveredPackageNotificationMessage message = mapper.toMessage(data);
        streamBridge.send(topic, message);
    }

    @Override
    public void sendServiceTaskRequest(String topic, ItemRefundProcess data) {
        ItemRefundProcessMessage message = mapper.toMessage(data);
        streamBridge.send(topic, message);
    }

    @Override
    public void sendServiceTaskRequest(String topic, CheckRefundProcess data) {
        CheckRefundProcessMessage message = mapper.toMessage(data);
        streamBridge.send(topic, message);
    }
}
