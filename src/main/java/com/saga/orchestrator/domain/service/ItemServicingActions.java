package com.saga.orchestrator.domain.service;

import com.saga.orchestrator.domain.in.ItemServicingActionApi;
import com.saga.orchestrator.domain.model.ItemServicingProcess;
import com.saga.orchestrator.domain.model.TopicUtils;
import com.saga.orchestrator.domain.out.WorkflowProducerApi;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class ItemServicingActions implements ItemServicingActionApi {

    private final WorkflowProducerApi workflowProducerApi;

    @Override
    public void createClaim(ItemServicingProcess process) {
        // produce message to claim
        workflowProducerApi.sendServiceTaskRequest(TopicUtils.CREATE_CLAIM_TOPIC, process);
    }
}
