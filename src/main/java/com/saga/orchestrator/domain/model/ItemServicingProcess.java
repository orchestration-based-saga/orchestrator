package com.saga.orchestrator.domain.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@RequiredArgsConstructor
@Getter
@Setter
@SuperBuilder
public class ItemServicingProcess extends WorkflowProcess {
    private final Claim claim;
}
