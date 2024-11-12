package com.saga.orchestrator.domain.model;

import lombok.experimental.UtilityClass;

@UtilityClass
public class TopicUtils {

    public final String CREATE_CLAIM_TOPIC = "workflow-claim-create-request";
    public final String UPDATE_CLAIM_TOPIC = "workflow-claim-update-request";
    public final String CREATE_SHIPMENT_TOPIC = "workflow-shipment-create-request";
    public final String ASSIGN_COURIER_TOPIC = "workflow-courier-assign-request";
    public final String NOTIFY_WAREHOUSE = "workflow-courier-notify-warehouse-request";
    public final String CHECK_DELIVERY = "workflow-shipment-check-delivery-request";
    public final String DELIVERED_PACKAGE = "workflow-delivered-package-request";
    public final String CHECK_REFUND = "workflow-check-refund-request";
}

