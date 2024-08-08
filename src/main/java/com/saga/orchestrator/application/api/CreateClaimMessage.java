package com.saga.orchestrator.application.api;

import java.util.UUID;

public record CreateClaimMessage(
        String orderId,
        Integer itemId,
        Integer merchantInventoryId,
        UUID customerId,
        UUID recipientId
) {
}
