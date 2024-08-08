package com.saga.orchestrator.infra.mapper;

import com.saga.orchestrator.domain.model.StateMachineInstance;
import com.saga.orchestrator.domain.model.WorkflowInstance;
import org.mapstruct.Mapper;

@Mapper
public interface WorkflowMapper {

    StateMachineInstance toDomain(WorkflowInstance instance);

}
