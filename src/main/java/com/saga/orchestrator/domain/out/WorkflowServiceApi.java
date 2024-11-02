package com.saga.orchestrator.domain.out;

import com.saga.orchestrator.domain.model.enums.WorkflowEvent;
import com.saga.orchestrator.domain.model.enums.WorkflowState;
import org.springframework.statemachine.StateMachine;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface WorkflowServiceApi {

    Mono<StateMachine<WorkflowState, WorkflowEvent>> createWorkflow();

    Flux<WorkflowState> triggerEvent(UUID workflowId, WorkflowEvent event, Object data);
}
