package com.saga.orchestrator.domain.in;

import com.saga.orchestrator.domain.model.ItemServicingProcess;

import java.util.UUID;

public interface ItemServicingApi {

    void itemServicing(ItemServicingProcess process);

    void claimCreated(UUID workflowId);
}
