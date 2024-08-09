package com.saga.orchestrator.domain.out;

import com.saga.orchestrator.domain.model.ItemServicingProcess;

public interface WorkflowProducerApi {

    void sendServiceTaskRequest(String topic, ItemServicingProcess data);
}
