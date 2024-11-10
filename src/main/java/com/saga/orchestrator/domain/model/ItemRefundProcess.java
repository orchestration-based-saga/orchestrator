package com.saga.orchestrator.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class ItemRefundProcess extends WorkflowProcess {
    private Claim claim;
    private boolean isForRefund;
    private BigDecimal refundAmount;
}
