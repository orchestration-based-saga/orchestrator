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
import org.springframework.statemachine.guard.Guard;

import java.util.EnumSet;
import java.util.UUID;

import static com.saga.orchestrator.domain.model.enums.WorkflowEvent.*;
import static com.saga.orchestrator.domain.model.enums.WorkflowState.*;

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
                .states(EnumSet.allOf(WorkflowState.class))
                .junction(USER_ACTION_RETURN_TO_WAREHOUSE_COMPLETED);

    }

    @Override
    public void configure(StateMachineTransitionConfigurer<WorkflowState, WorkflowEvent> transitions) throws Exception {
        transitions
                .withExternal()
                .source(ITEM_SERVICING_INITIATED).target(USER_ACTION_RETURN_TO_WAREHOUSE).event(CREATE_CLAIM)
                .action(createClaim())
                .and()
                .withExternal()
                .source(USER_ACTION_RETURN_TO_WAREHOUSE).target(USER_ACTION_RETURN_TO_WAREHOUSE_COMPLETED)
                .event(CLAIM_CREATED)
                .and()
                // user decided that item should/should not be returned to warehouse
                .withJunction()
                    .source(USER_ACTION_RETURN_TO_WAREHOUSE_COMPLETED)
                    .first(SERVICE_ON_SITE, doNotReturnToWarehouse())
                    .then(CREATE_SHIPMENT, returnToWarehouse(), createShipment())
                    .last(IS_FOR_REFUND)
                .and()
                .withExternal()
                .source(CREATE_SHIPMENT).target(ASSIGN_COURIER).event(SHIPMENT_CREATED)
                .action(updateClaim()).action(assignCourier())
        ;
    }

    @Bean
    public Action<WorkflowState, WorkflowEvent> createClaim() {
        return context -> {
            Object data = context.getMessageHeader("data");
            UUID workflowId = (UUID) context.getMessageHeader("workflowId");
            if (data == null) {
                log.error("Can't create claim");
                // todo throw an error
            }
            if (data instanceof ItemServicingProcess) {
                itemServicingActionApi.createClaim((ItemServicingProcess) data, workflowId);
            }
        };
    }

    @Bean
    public Action<WorkflowState, WorkflowEvent> updateClaim() {
        return context -> {
            Object data = context.getMessageHeader("data");
            UUID workflowId = (UUID) context.getMessageHeader("workflowId");
            if (data == null) {
                log.error("Can't update claim");
                // todo throw an error
            }
            if (data instanceof ItemServicingProcess) {
                itemServicingActionApi.updateClaim((ItemServicingProcess) data, workflowId);
            }
        };
    }

    @Bean
    public Action<WorkflowState, WorkflowEvent> assignCourier() {
        return context -> {
            Object data = context.getMessageHeader("data");
            UUID workflowId = (UUID) context.getMessageHeader("workflowId");
            if (data == null) {
                log.error("Can't assign courier");
                // todo throw an error
            }
            if (data instanceof ItemServicingProcess) {
                itemServicingActionApi.assignCourier((ItemServicingProcess) data, workflowId);
            }
        };
    }

    @Bean
    public Action<WorkflowState, WorkflowEvent> createShipment() {
        return context -> {
            Object data = context.getMessageHeader("data");
            UUID workflowId = (UUID) context.getMessageHeader("workflowId");
            if (data == null) {
                log.error("Can't create shipment");
                // todo throw an error
            }
            if (data instanceof ItemServicingProcess) {
                itemServicingActionApi.createShipment((ItemServicingProcess) data, workflowId);
            }
        };
    }

    @Bean
    public Guard<WorkflowState, WorkflowEvent> doNotReturnToWarehouse() {
        return context -> {
            Object data = context.getMessageHeader("data");
            if (data == null) {
                log.error("Can't create shipment");
                // todo throw an error
            }
            if (data instanceof ItemServicingProcess) {
                return !((ItemServicingProcess) data).getClaim().shipmentInitiated();
            }
            return false;
        };
    }



    @Bean
    public Guard<WorkflowState, WorkflowEvent> returnToWarehouse() {
        return context -> {
            Object data = context.getMessageHeader("data");
            if (data == null) {
                log.error("Can't create shipment");
                // todo throw an error
            }
            if (data instanceof ItemServicingProcess) {
                return ((ItemServicingProcess) data).getClaim().shipmentInitiated();
            }
            return false;
        };
    }
}
