package com.saga.orchestrator.domain.in;

import com.saga.orchestrator.domain.model.ItemServicingProcess;

public interface ItemServicingApi {

    void itemServicing(ItemServicingProcess process);

    void claimCreated(String businessKey, ItemServicingProcess process);
}
