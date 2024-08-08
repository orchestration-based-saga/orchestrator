package com.saga.orchestrator.domain.model;

import com.saga.orchestrator.domain.model.enums.WorkflowState;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
public class WorkflowProcess {
    private Long id;
    private String processId;
    private String businessKey;
    private Long parentProcessId;
    private UUID workflow;
    public WorkflowState state;
}
