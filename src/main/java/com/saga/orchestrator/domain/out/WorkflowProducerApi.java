package com.saga.orchestrator.domain.out;

import com.saga.orchestrator.domain.model.*;

public interface WorkflowProducerApi {

    void sendServiceTaskRequest(String topic, ItemServicingProcess data);

    void sendServiceTaskRequest(String topic, ShipmentProcess data);

    void sendServiceTaskRequest(String topic, CheckDeliveryProcess data);

    void sendServiceTaskRequest(String topic, DeliveredPackageNotification data);

    void sendServiceTaskRequest(String topic, ItemRefundProcess data);
}
