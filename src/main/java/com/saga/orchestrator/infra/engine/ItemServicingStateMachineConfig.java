package com.saga.orchestrator.infra.engine;

import com.saga.orchestrator.domain.model.enums.WorkflowEvent;
import com.saga.orchestrator.domain.model.enums.WorkflowState;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import static com.saga.orchestrator.domain.model.enums.WorkflowEvent.CREATE_CLAIM;
import static com.saga.orchestrator.domain.model.enums.WorkflowState.ITEM_SERVICING_INITIATED;
import static com.saga.orchestrator.domain.model.enums.WorkflowState.USER_ACTION_RETURN_TO_WAREHOUSE;

@Configuration
@EnableStateMachineFactory(name = "itemServicingStateMachineFactory")
public class ItemServicingStateMachineConfig extends EnumStateMachineConfigurerAdapter<WorkflowState, WorkflowEvent> {

    @Override
    public void configure(StateMachineStateConfigurer<WorkflowState, WorkflowEvent> states) throws Exception {
        states.withStates()
                .initial(ITEM_SERVICING_INITIATED);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<WorkflowState, WorkflowEvent> transitions) throws Exception {
        transitions
                .withExternal()
                .source(ITEM_SERVICING_INITIATED).target(USER_ACTION_RETURN_TO_WAREHOUSE).event(CREATE_CLAIM);
    }

}
