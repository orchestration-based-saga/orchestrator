package com.saga.orchestrator.application.api;

import java.math.BigDecimal;
import java.util.UUID;

public record ItemRefundProcessMessage(
        Long id,
        String processId,
        String businessKey,
        Long parentProcessId,
        UUID workflow,
        Integer itemId,
        BigDecimal amount
) {
}
