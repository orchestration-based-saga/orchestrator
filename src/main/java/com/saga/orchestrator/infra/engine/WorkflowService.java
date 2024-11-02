package com.saga.orchestrator.infra.engine;

import com.saga.orchestrator.domain.model.enums.WorkflowEvent;
import com.saga.orchestrator.domain.model.enums.WorkflowState;
import com.saga.orchestrator.domain.out.WorkflowServiceApi;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.service.StateMachineService;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class WorkflowService implements WorkflowServiceApi {

    private final StateMachineService<WorkflowState, WorkflowEvent> stateMachineService;

    @Override
    public Mono<StateMachine<WorkflowState, WorkflowEvent>> createWorkflow() {
        var machineId = UUID.randomUUID();
        return getMachine(machineId.toString());
    }

    @Override
    public Flux<WorkflowState> triggerEvent(UUID workflowId, WorkflowEvent event, Object data) {
        return getMachine(workflowId.toString())
                .flatMapMany(sm -> sm.sendEvent(wrapEvent(event, workflowId, data)))
                .map(res -> res.getRegion().getState().getId());
    }

    private Mono<Message<WorkflowEvent>> wrapEvent(WorkflowEvent event, UUID workflowId, Object data) {
        return Mono.just(MessageBuilder
                .withPayload(event)
                .setHeader("workflowId", workflowId)
                .setHeader("data", data)
                .build());
    }

    private Mono<StateMachine<WorkflowState, WorkflowEvent>> getMachine(String id) {
        return Mono.just(id).publishOn(Schedulers.boundedElastic())
                .map(stateMachineService::acquireStateMachine);
    }
}
