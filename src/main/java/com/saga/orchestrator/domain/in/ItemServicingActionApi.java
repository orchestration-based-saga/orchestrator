package com.saga.orchestrator.domain.in;

import com.saga.orchestrator.domain.model.ItemServicingProcess;

public interface ItemServicingActionApi {

    void createClaim(ItemServicingProcess process);

}
