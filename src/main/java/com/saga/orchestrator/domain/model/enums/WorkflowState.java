package com.saga.orchestrator.domain.model.enums;

public enum WorkflowState {
    ITEM_SERVICING_INITIATED,
    USER_ACTION_RETURN_TO_WAREHOUSE,
    USER_ACTION_RETURN_TO_WAREHOUSE_COMPLETED,
    SERVICE_ON_SITE,
    IS_FOR_REFUND,
    CREATE_SHIPMENT
}
