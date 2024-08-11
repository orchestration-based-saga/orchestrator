package com.saga.orchestrator.application.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.saga.orchestrator.application.api.enums.ClaimStatus;

import java.math.BigDecimal;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ClaimResponse(
        Integer id,
        String orderId,
        Integer itemId,
        Integer merchantInventoryId,
        Integer shipmentId,
        BigDecimal refundAmount,
        ClaimStatus status,
        UUID customerId,
        UUID recipientId,
        Boolean shipmentInitiated,
        String packageId
) {
}
