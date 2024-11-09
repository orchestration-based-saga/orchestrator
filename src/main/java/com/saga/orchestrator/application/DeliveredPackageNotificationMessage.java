package com.saga.orchestrator.application;

import java.util.UUID;

public record DeliveredPackageNotificationMessage(
        Long id,
        String processId,
        String businessKey,
        Long parentProcessId,
        UUID workflow,
        String packageId
) {
}
