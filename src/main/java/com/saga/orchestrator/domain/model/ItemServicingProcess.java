package com.saga.orchestrator.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@Getter
@Setter
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
