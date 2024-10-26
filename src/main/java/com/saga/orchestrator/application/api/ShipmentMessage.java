package com.saga.orchestrator.application.api;

import com.saga.orchestrator.application.api.enums.ShipmentState;

public record ShipmentMessage(
        Integer shipmentId,
        String orderId,
        Integer merchantInventoryId,
        Integer itemId,
        String packageId,
        ShipmentState status
) {
}
