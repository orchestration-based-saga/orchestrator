package com.saga.orchestrator.domain.in;

import com.saga.orchestrator.domain.model.*;

public interface ItemServicingApi {

    void itemServicing(ItemServicingProcess process);

    void claimCreated(String businessKey, ItemServicingProcess process);

    void shipmentCreated(String businessKey, ItemServicingProcess process);

    void courierAssigned(String businessKey, ShipmentProcess process);

    void isPackageDelivered(String businessKey, CheckDeliveryProcess process);

    void warehouseNotified(String businessKey, WarehouseNotified process);

    void notifiedOfDelivery(String businessKey, String processId);

    void startRefund(ItemRefundProcess process);
}
