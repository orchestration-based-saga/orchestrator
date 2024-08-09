package com.saga.orchestrator.application.api;

import java.util.UUID;

public record ItemServicingProcessMessage(
        Long id,
        String processId,
        String businessKey,
        Long parentProcessId,
        UUID workflow,
        CreateClaimMessage claim
) {
}
