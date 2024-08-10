package com.saga.orchestrator.infra.engine;

import com.saga.orchestrator.domain.model.StateMachineInstance;
import com.saga.orchestrator.domain.model.WorkflowInstance;
import com.saga.orchestrator.domain.model.enums.WorkflowEvent;
import com.saga.orchestrator.domain.model.enums.WorkflowState;
import com.saga.orchestrator.domain.out.WorkflowServiceApi;
import com.saga.orchestrator.infra.mapper.WorkflowMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class WorkflowService implements WorkflowServiceApi {

    private final Map<UUID, WorkflowInstance> workflows = new ConcurrentHashMap<>();

    @Qualifier("itemServicingStateMachineFactory")
    private final StateMachineFactory<WorkflowState, WorkflowEvent> stateMachineFactory;
    private final WorkflowMapper mapper;

    @Override
    public StateMachineInstance createWorkflow() {
        StateMachine<WorkflowState, WorkflowEvent> stateMachine = stateMachineFactory.getStateMachine(UUID.randomUUID());
        WorkflowInstance workflowInstance = new WorkflowInstance(stateMachine);
        workflowInstance.getStateMachine().startReactively().subscribe();
        workflows.put(workflowInstance.getId(), workflowInstance);
        return mapper.toDomain(workflowInstance);
    }

    @Override
    public void triggerEvent(UUID workflowId, WorkflowEvent event, Object data) {
        WorkflowInstance workflowInstance = workflows.get(workflowId);
        if (workflowInstance != null) {
            StateMachine<WorkflowState, WorkflowEvent> stateMachine = workflowInstance.getStateMachine();
            stateMachine.sendEvent(wrapEvent(event, workflowInstance.getId(), data)).subscribe();
        }
    }

    @Override
    public StateMachineInstance getWorkflow(UUID workflowId) {
        return mapper.toDomain(workflows.get(workflowId));
    }

    private Mono<Message<WorkflowEvent>> wrapEvent(WorkflowEvent event, UUID workflowId, Object data) {
        return Mono.just(MessageBuilder
                .withPayload(event)
                .setHeader("workflowId", workflowId)
                .setHeader("data", data)
                .build());
    }
}
