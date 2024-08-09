package com.saga.orchestrator.domain.model;

import java.util.UUID;


public record Claim(
        String orderId,
        Integer itemId,
        Integer merchantInventoryId,
        UUID customerId,
        UUID recipientId
) {
}
