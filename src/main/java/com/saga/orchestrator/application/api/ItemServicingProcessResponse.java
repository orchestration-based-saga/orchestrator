package com.saga.orchestrator.application.api;

import com.saga.orchestrator.domain.model.Shipment;

import java.util.UUID;

public record ItemServicingProcessResponse(
        Long id,
        String processId,
        String businessKey,
        Long parentProcessId,
        UUID workflow,
        ClaimResponse claim,
        Shipment shipment
) {
}

