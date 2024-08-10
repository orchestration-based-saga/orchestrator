package com.saga.orchestrator.application.mapper;

import com.saga.orchestrator.application.api.*;
import com.saga.orchestrator.domain.model.Claim;
import com.saga.orchestrator.domain.model.ItemServicingProcess;
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

    @Named("toClaim")
    default Claim toClaim(Object data) {
        try {
            if (data instanceof CreateClaimMessage claimMessage) {
                return new Claim(
                        claimMessage.orderId(),
                        claimMessage.itemId(),
                        claimMessage.merchantInventoryId(),
                        claimMessage.customerId(),
                        claimMessage.recipientId(),
                        null);
            }
        } catch (ClassCastException e) {
            return null;
        }
        return null;
    }

    ItemServicingProcessMessage toMessage(ItemServicingProcess process);
}
