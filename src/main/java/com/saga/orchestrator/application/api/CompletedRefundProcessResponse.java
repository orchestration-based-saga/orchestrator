package com.saga.orchestrator.application.api;

public record CompletedRefundProcessResponse(
        String processId,
        String businessKey,
        boolean completed
) {
}
