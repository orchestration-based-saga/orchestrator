package com.saga.orchestrator.domain.model;

import com.saga.orchestrator.domain.model.enums.ClaimState;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public final class Claim {
    private Integer id;
    private String orderId;
    private Integer itemId;
    private Integer merchantInventoryId;
    private Integer shipmentId;
    private UUID customerId;
    private UUID recipientId;
    private Boolean shipmentInitiated;
    private ClaimState status;
    private String packageId;
}
