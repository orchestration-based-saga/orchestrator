package com.saga.orchestrator.application.api;

import java.math.BigDecimal;

public record ItemRefundProcessRequest(
        String processId,
        String businessKey,
        boolean isForRefund,
        BigDecimal refundAmount,
        ClaimResponse claim
) {
}
