package com.saga.orchestrator.domain.model;

import com.saga.orchestrator.domain.model.enums.WorkflowEvent;
import com.saga.orchestrator.domain.model.enums.WorkflowState;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.statemachine.StateMachine;

import java.util.UUID;


@RequiredArgsConstructor
@Getter
public class StateMachineInstance {
    private final UUID id = UUID.randomUUID();
    private final StateMachine<WorkflowState, WorkflowEvent> stateMachine;

    public WorkflowState getCurrentState() {
        return stateMachine.getState().getId();
    }
}
