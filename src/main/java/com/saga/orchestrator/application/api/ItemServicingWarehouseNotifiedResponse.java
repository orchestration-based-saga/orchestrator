package com.saga.orchestrator.application.api;

public record ItemServicingWarehouseNotifiedResponse(
        String processId,
        String businessKey,
        boolean success
) {
}
