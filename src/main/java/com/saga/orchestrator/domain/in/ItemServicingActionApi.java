package com.saga.orchestrator.domain.in;

import com.saga.orchestrator.domain.model.CheckDeliveryProcess;
import com.saga.orchestrator.domain.model.ItemRefundProcess;
import com.saga.orchestrator.domain.model.ItemServicingProcess;
import com.saga.orchestrator.domain.model.ShipmentProcess;

import java.util.UUID;

public interface ItemServicingActionApi {

    void createClaim(ItemServicingProcess process, UUID workflowId);

    void updateClaim(ItemServicingProcess process, UUID workflowId);

    void createShipment(ItemServicingProcess process, UUID workflowId);

    void assignCourier(ItemServicingProcess process, UUID workflowId);

    void notifyWarehouse(ShipmentProcess process, UUID workflowId);

    void checkIfPackageIsDelivered(String businessKey, String packageId, UUID workflowId);

    void notifyOfDeliveredPackage(CheckDeliveryProcess process, UUID workflowId);

    void initiateRefund(ItemRefundProcess process, UUID workflowId);
}
