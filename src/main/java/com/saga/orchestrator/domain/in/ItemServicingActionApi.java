package com.saga.orchestrator.domain.in;

import com.saga.orchestrator.domain.model.ItemServicingProcess;

import java.util.UUID;

public interface ItemServicingActionApi {

    void createClaim(ItemServicingProcess process, UUID workflowId);

}
