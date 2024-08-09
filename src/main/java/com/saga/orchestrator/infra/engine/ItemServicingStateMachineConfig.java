package com.saga.orchestrator.infra.engine;

import com.saga.orchestrator.domain.in.ItemServicingActionApi;
import com.saga.orchestrator.domain.model.ItemServicingProcess;
import com.saga.orchestrator.domain.model.enums.WorkflowEvent;
import com.saga.orchestrator.domain.model.enums.WorkflowState;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import java.util.EnumSet;

import static com.saga.orchestrator.domain.model.enums.WorkflowEvent.CREATE_CLAIM;
import static com.saga.orchestrator.domain.model.enums.WorkflowState.ITEM_SERVICING_INITIATED;
import static com.saga.orchestrator.domain.model.enums.WorkflowState.USER_ACTION_RETURN_TO_WAREHOUSE;

@Configuration
@Slf4j
@RequiredArgsConstructor
@EnableStateMachineFactory(name = "itemServicingStateMachineFactory")
public class ItemServicingStateMachineConfig extends EnumStateMachineConfigurerAdapter<WorkflowState, WorkflowEvent> {

    private final ItemServicingActionApi itemServicingActionApi;


    @Override
    public void configure(StateMachineStateConfigurer<WorkflowState, WorkflowEvent> states) throws Exception {
        states.withStates()
                .initial(ITEM_SERVICING_INITIATED)
                .states(EnumSet.allOf(WorkflowState.class));
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<WorkflowState, WorkflowEvent> transitions) throws Exception {
        transitions
                .withExternal()
                .source(ITEM_SERVICING_INITIATED).target(USER_ACTION_RETURN_TO_WAREHOUSE).event(CREATE_CLAIM)
                .action(createClaim());
    }

    @Bean
    public Action<WorkflowState, WorkflowEvent> createClaim() {
        return context -> {
            Object data = context.getMessageHeader("data");
            if (data == null) {
                log.error("Can't create claim");
                // todo throw an error
            }
            if (data instanceof ItemServicingProcess) {
                itemServicingActionApi.createClaim((ItemServicingProcess) data);
            }
        };
    }
}
