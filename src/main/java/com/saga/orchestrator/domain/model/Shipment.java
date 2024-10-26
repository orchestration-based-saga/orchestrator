package com.saga.orchestrator.domain.model;

import com.saga.orchestrator.domain.model.enums.ShipmentStatus;

public record Shipment(
        Integer shipmentId,
        String orderId,
        Integer merchantInventoryId,
        Integer itemId,
        String packageId,
        ShipmentStatus status
) {
}
