package com.saga.orchestrator.domain.model;

import com.saga.orchestrator.domain.model.enums.ShipmentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Shipment {
    private Integer shipmentId;
    private String orderId;
    private Integer merchantInventoryId;
    private Integer itemId;
    private String packageId;
    private ShipmentStatus status;
}
