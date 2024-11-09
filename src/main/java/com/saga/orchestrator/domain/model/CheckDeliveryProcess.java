package com.saga.orchestrator.domain.model;

import lombok.*;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SuperBuilder
public class CheckDeliveryProcess extends WorkflowProcess{
    private String packageId;
    private Boolean isDelivered;
}
