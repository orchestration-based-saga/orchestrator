package com.saga.orchestrator.domain.in;

import com.saga.orchestrator.domain.model.CheckDeliveryProcess;
import com.saga.orchestrator.domain.model.ItemServicingProcess;
import com.saga.orchestrator.domain.model.ShipmentProcess;

public interface ItemServicingApi {

    void itemServicing(ItemServicingProcess process);

    void claimCreated(String businessKey, ItemServicingProcess process);

    void shipmentCreated(String businessKey, ItemServicingProcess process);

    void courierAssigned(String businessKey, ShipmentProcess process);

    void isPackageDelivered(String businessKey, CheckDeliveryProcess process);
}
