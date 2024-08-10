package com.saga.orchestrator.application.api;

import java.util.UUID;

public record ItemServicingProcessResponse(
        Long id,
        String processId,
        String businessKey,
        Long parentProcessId,
        UUID workflow,
        ClaimResponse claim
) {
}

