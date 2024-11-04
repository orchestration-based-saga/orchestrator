package com.saga.orchestrator.infra.engine;

import com.saga.orchestrator.domain.in.ItemServicingActionApi;
import com.saga.orchestrator.domain.model.CheckDeliveryProcess;
import com.saga.orchestrator.domain.model.ItemServicingProcess;
import com.saga.orchestrator.domain.model.ShipmentProcess;
import com.saga.orchestrator.domain.model.enums.WorkflowEvent;
import com.saga.orchestrator.domain.model.enums.WorkflowState;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.guard.Guard;
import org.springframework.statemachine.persist.StateMachineRuntimePersister;

import java.util.EnumSet;
import java.util.UUID;

import static com.saga.orchestrator.domain.model.enums.WorkflowEvent.*;
import static com.saga.orchestrator.domain.model.enums.WorkflowState.*;

@Configuration
@Slf4j
@RequiredArgsConstructor
@EnableStateMachineFactory(name = "itemServicingStateMachineFactory")
public class ItemServicingStateMachineConfig extends EnumStateMachineConfigurerAdapter<WorkflowState, WorkflowEvent> {

    private final ActionsConfig actions;
    private final GuardsConfig guards;
    private final StateMachineRuntimePersister<WorkflowState, WorkflowEvent, String> stateMachineRuntimePersister;
    // 5 MINUTES
    private final long DELIVERY_TIMEOUT_PERIOD = 300000;

    @Override
    public void configure(StateMachineConfigurationConfigurer<WorkflowState, WorkflowEvent> config)
            throws Exception {
        config
                .withPersistence()
                .runtimePersister(stateMachineRuntimePersister);
    }

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
                    .action(actions.createClaim())
                    .and()
                .withExternal()
                    .source(USER_ACTION_RETURN_TO_WAREHOUSE).target(USER_ACTION_RETURN_TO_WAREHOUSE_COMPLETED)
                    .event(CLAIM_CREATED)
                    .and()
                // user decided that item should/should not be returned to warehouse
                .withJunction()
                    .source(USER_ACTION_RETURN_TO_WAREHOUSE_COMPLETED)
                    .first(SERVICE_ON_SITE, guards.doNotReturnToWarehouse())
                    .then(CREATE_SHIPMENT, guards.returnToWarehouse(), actions.createShipment())
                    .last(IS_FOR_REFUND)
                    .and()
                .withExternal()
                    .source(CREATE_SHIPMENT).target(ASSIGN_COURIER).event(SHIPMENT_CREATED)
                    .action(actions.updateClaim()).action(actions.assignCourierToShipment()).action(actions.addRequestToContext())
                    .and()
                .withExternal()
                    .source(ASSIGN_COURIER).target(ASSIGN_COURIER_COMPLETED).event(COURIER_ASSIGNED)
                    .action(actions.notifyWarehouseOfIncomingDelivery())
                    .and()
                .withExternal()
                    .source(ASSIGN_COURIER_COMPLETED).target(WAIT_FOR_DELIVERY).event(WAREHOUSE_NOTIFIED)
                    .and()
                .withInternal()
                    .source(WAIT_FOR_DELIVERY)
                    // if not reassign courier, else update status on courier and shipment
                    .action(actions.checkIfDelivered())
                    .timer(DELIVERY_TIMEOUT_PERIOD)
                    .and()
                .withJunction()
                    .source(WAIT_FOR_DELIVERY)
                    .first(WAIT_FOR_DELIVERY, guards.isNotDelivered(), actions.reassignCourier())
                    .then(DELIVERED, guards.isDelivered(), actions.updateShipment())
                    .last(WAIT_FOR_DELIVERY)
//                .and()
//                    .withExternal()
//                    .source()
//                    .source(WAIT_FOR_DELIVERY).target(NOT_DELIVERED).event(PACKAGE_NOT_DELIVERED)
//                    .action(reassignCourier())
//                    .and()
//                .withExternal()
//                    .source(WAIT_FOR_DELIVERY).target(DELIVERED).event(PACKAGE_DELIVERED)
//                .action(updateShipment())
        // when shipment, courier and claim are updated transition to is for refund state
        ;
    }
}
