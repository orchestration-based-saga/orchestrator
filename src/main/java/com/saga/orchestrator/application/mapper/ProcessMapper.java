package com.saga.orchestrator.application.mapper;

import com.saga.orchestrator.application.api.WorkflowStartProcessMessage;
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

    @Named("toClaim")
    default Claim toClaim(Object data) {
        try {
            return (Claim) data;
        } catch (ClassCastException e) {
            return null;
        }
    }
}