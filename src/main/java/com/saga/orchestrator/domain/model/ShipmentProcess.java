package com.saga.orchestrator.domain.model;
import lombok.*;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
public class ShipmentProcess extends WorkflowProcess{
    private Shipment shipment;
}
