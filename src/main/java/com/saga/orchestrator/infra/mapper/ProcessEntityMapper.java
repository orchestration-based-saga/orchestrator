package com.saga.orchestrator.infra.mapper;

import com.saga.orchestrator.domain.model.WorkflowProcess;
import com.saga.orchestrator.infra.model.ProcessEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface ProcessEntityMapper {

    @Mapping(target = "workflow", source = "workflowId")
    WorkflowProcess toDomain(ProcessEntity processEntity);

    @Mapping(target = "workflowId", source = "workflow")
    ProcessEntity toEntity(WorkflowProcess processEntity);
}
