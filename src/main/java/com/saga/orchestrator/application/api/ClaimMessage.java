package com.saga.orchestrator.application.api;

import com.saga.orchestrator.application.api.enums.ClaimStatus;

import java.util.UUID;

public record ClaimMessage(
        Integer id,
        String orderId,
        Integer itemId,
        Integer merchantInventoryId,
        UUID customerId,
        UUID recipientId,
        ClaimStatus status,
        Integer shipmentId,
        String packageId
) {
}
