package com.saga.orchestrator.infra.config;

import com.saga.orchestrator.domain.model.enums.WorkflowEvent;
import com.saga.orchestrator.domain.model.enums.WorkflowState;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.data.jpa.JpaPersistingStateMachineInterceptor;
import org.springframework.statemachine.data.jpa.JpaStateMachineRepository;
import org.springframework.statemachine.persist.StateMachineRuntimePersister;
import org.springframework.statemachine.service.DefaultStateMachineService;
import org.springframework.statemachine.service.StateMachineService;

@Configuration
public class StateMachinePersistenceConfig {

    @Bean
    public StateMachineRuntimePersister<WorkflowState, WorkflowEvent, String> stateMachineRuntimePersister(
            JpaStateMachineRepository jpaStateMachineRepository) {
        return new JpaPersistingStateMachineInterceptor<>(jpaStateMachineRepository);
    }

    @Bean
    public StateMachineService<WorkflowState, WorkflowEvent> stateMachineService(
            StateMachineFactory<WorkflowState, WorkflowEvent> stateMachineFactory,
            StateMachineRuntimePersister<WorkflowState, WorkflowEvent, String> stateMachineRuntimePersister) {
        return new DefaultStateMachineService<>(stateMachineFactory, stateMachineRuntimePersister);
    }
}
