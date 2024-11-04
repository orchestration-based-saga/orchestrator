package com.saga.orchestrator.domain.model.enums;

public enum WorkflowEvent {
    CREATE_CLAIM,
    CLAIM_CREATED,
    SHIPMENT_CREATED,
    COURIER_ASSIGNED,
    WAREHOUSE_NOTIFIED,
    PACKAGE_NOT_DELIVERED,
    PACKAGE_DELIVERED
}
