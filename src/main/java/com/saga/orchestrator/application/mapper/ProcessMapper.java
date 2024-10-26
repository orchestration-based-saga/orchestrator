package com.saga.orchestrator.application.mapper;

import com.saga.orchestrator.application.api.*;
import com.saga.orchestrator.domain.model.CheckDeliveryProcess;
import com.saga.orchestrator.domain.model.Claim;
import com.saga.orchestrator.domain.model.ItemServicingProcess;
import com.saga.orchestrator.domain.model.ShipmentProcess;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper
public interface ProcessMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "parentProcessId", ignore = true)
    @Mapping(target = "state", ignore = true)
    @Mapping(target = "workflow", ignore = true)
    @Mapping(target = "claim", source = "message.data", qualifiedByName = "toClaim")
    ItemServicingProcess toItemServicingProcess(WorkflowStartProcessMessage message);

    Claim fromResponse(ClaimResponse response);

    @Mapping(target = "state", ignore = true)
    ItemServicingProcess fromResponse(ItemServicingProcessResponse response);

    @Mapping(target = "state", ignore = true)
    ShipmentProcess fromResponse(ItemServicingProcessShipmentResponse response);

    @Named("toClaim")
    default Claim toClaim(Object data) {
        try {
            if (data instanceof ClaimMessage claimMessage) {
                return new Claim(
                        claimMessage.id(),
                        claimMessage.orderId(),
                        claimMessage.itemId(),
                        claimMessage.merchantInventoryId(),
                        null,
                        claimMessage.customerId(),
                        claimMessage.recipientId(),
                        null,
                        null,
                        null);
            }
        } catch (ClassCastException e) {
            return null;
        }
        return null;
    }

    ItemServicingProcessMessage toMessage(ItemServicingProcess process);

    @Mapping(target = "shipmentId", source = "process.shipment.shipmentId")
    ShipmentProcessMessage toMessage(ShipmentProcess process);

    CheckDeliveryProcessMessage toMessage(CheckDeliveryProcess process);

    @Mapping(target = "packageId", source = "response.response.packageId")
    @Mapping(target = "isDelivered", source = "response.response.isDelivered")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "parentProcessId", ignore = true)
    @Mapping(target = "workflow", ignore = true)
    @Mapping(target = "state", ignore = true)
    CheckDeliveryProcess fromResponse(CheckDeliveryResponse response);
}
