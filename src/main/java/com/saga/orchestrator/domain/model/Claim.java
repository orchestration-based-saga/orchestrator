package com.saga.orchestrator.domain.model;

import com.saga.orchestrator.domain.model.enums.ClaimState;

import java.util.UUID;

public record Claim(
        Integer id,
        String orderId,
        Integer itemId,
        Integer merchantInventoryId,
        Integer shipmentId,
        UUID customerId,
        UUID recipientId,
        Boolean shipmentInitiated,
        ClaimState status,
        String packageId
) {
}
