package com.saga.orchestrator.domain.out;

import com.saga.orchestrator.domain.model.CheckDeliveryProcess;
import com.saga.orchestrator.domain.model.ItemServicingProcess;
import com.saga.orchestrator.domain.model.ShipmentProcess;

public interface WorkflowProducerApi {

    void sendServiceTaskRequest(String topic, ItemServicingProcess data);

    void sendServiceTaskRequest(String topic, ShipmentProcess data);

    void sendServiceTaskRequest(String topic, CheckDeliveryProcess data);
}
