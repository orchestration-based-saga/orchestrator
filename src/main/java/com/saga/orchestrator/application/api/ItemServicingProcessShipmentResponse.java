package com.saga.orchestrator.application.api;

import java.util.UUID;

public record ItemServicingProcessShipmentResponse(
        Long id,
        String processId,
        String businessKey,
        Long parentProcessId,
        UUID workflow,
        ShipmentMessage shipment
) {
}
