package com.saga.orchestrator.domain.model;

import lombok.*;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class ItemServicingProcess extends WorkflowProcess {
    private Claim claim;

    public void updateWorkflowProcess(WorkflowProcess process) {
        this.setProcessId(process.getProcessId());
        this.setWorkflow(process.getWorkflow());
        this.setId(process.getId());
        this.setParentProcessId(process.getParentProcessId());
        this.setBusinessKey(process.getBusinessKey());
        this.setState(process.getState());
    }
}
