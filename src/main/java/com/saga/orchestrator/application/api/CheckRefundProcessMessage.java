package com.saga.orchestrator.application.api;

import java.util.UUID;

public record CheckRefundProcessMessage(
        String processId,
        String businessKey,
        Long parentProcessId,
        UUID workflow,
        String orderId
) {

}
