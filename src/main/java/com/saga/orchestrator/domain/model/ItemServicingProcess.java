package com.saga.orchestrator.domain.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@RequiredArgsConstructor
@Getter
@Setter
@SuperBuilder
public class ItemServicingProcess extends WorkflowProcess {
    private final Claim claim;

    public void updateWorkflowProcess(WorkflowProcess process) {
        this.setProcessId(process.getProcessId());
        this.setWorkflow(process.getWorkflow());
        this.setId(process.getId());
        this.setParentProcessId(process.getParentProcessId());
        this.setBusinessKey(process.getBusinessKey());
        this.setState(process.getState());
    }
}
