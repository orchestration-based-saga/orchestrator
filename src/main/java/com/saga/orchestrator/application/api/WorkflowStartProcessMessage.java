package com.saga.orchestrator.application.api;

import lombok.Builder;

@Builder
public record WorkflowStartProcessMessage(
        String processId,
        String businessKey,
        Object data
) {
}

