package com.saga.orchestrator.domain.in;

import com.saga.orchestrator.domain.model.ItemServicingProcess;

import java.util.UUID;

public interface ItemServicingActionApi {

    void createClaim(ItemServicingProcess process, UUID workflowId);

    void updateClaim(ItemServicingProcess process, UUID workflowId);

    void createShipment(ItemServicingProcess process, UUID workflowId);

    void assignCourier(ItemServicingProcess process, UUID workflowId);
}
