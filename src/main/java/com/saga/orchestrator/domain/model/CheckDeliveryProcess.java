package com.saga.orchestrator.domain.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@RequiredArgsConstructor
@Getter
@Setter
@SuperBuilder
public class CheckDeliveryProcess extends WorkflowProcess{
    private final String packageId;
    private final Boolean isDelivered;
}
