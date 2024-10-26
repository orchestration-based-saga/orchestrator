package com.saga.orchestrator.application.api;

import java.util.UUID;

public record CheckDeliveryProcessMessage(
        Long id,
        String processId,
        String businessKey,
        Long parentProcessId,
        UUID workflow,
        String packageId
) {

}
