package com.saga.orchestrator.application.api;

import com.saga.orchestrator.application.api.enums.ClaimStatus;

import java.math.BigDecimal;
import java.util.UUID;

public record ClaimResponse(
        Integer id,
        String orderId,
        Integer itemId,
        Integer merchantInventoryId,
        Integer shipmentId,
        BigDecimal refundAmount,
        ClaimStatus status,
        UUID customerId,
        UUID recipientId
) {
}
