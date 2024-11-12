package com.saga.orchestrator.domain.in;

import com.saga.orchestrator.domain.model.*;

import java.util.UUID;

public interface ItemServicingActionApi {

    void createClaim(ItemServicingProcess process, UUID workflowId);

    void updateClaim(ItemServicingProcess process, UUID workflowId);

    void createShipment(ItemServicingProcess process, UUID workflowId);

    void assignCourier(ItemServicingProcess process, UUID workflowId);

    void notifyWarehouse(ShipmentProcess process, UUID workflowId);

    void checkIfPackageIsDelivered(String businessKey, String packageId, UUID workflowId);

    void notifyOfDeliveredPackage(CheckDeliveryProcess process, UUID workflowId);

    void checkIfRefundCompleted(String processId, String orderId, UUID workflowId, String businessKey);
}
