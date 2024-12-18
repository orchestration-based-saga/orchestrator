package com.saga.orchestrator.infra.engine;

import com.saga.orchestrator.domain.model.enums.WorkflowEvent;
import com.saga.orchestrator.domain.model.enums.WorkflowState;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.persist.StateMachineRuntimePersister;

import java.util.EnumSet;

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
    // 3 MINUTES
    private final long DELIVERY_TIMEOUT_PERIOD = 180000;
    private final long REFUND_TIMEOUT_PERIOD = 120000;

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
                .junction(USER_ACTION_RETURN_TO_WAREHOUSE_COMPLETED)
                .choice(USER_ACTION_IS_FOR_REFUND_RESULT)
                .end(ITEM_SERVICING_COMPLETED);

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
                    .last(USER_ACTION_IS_FOR_REFUND)
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
                    .action(actions.checkIfDelivered())
                    .timer(DELIVERY_TIMEOUT_PERIOD)
                    .and()
                .withExternal()
                    .source(WAIT_FOR_DELIVERY).target(WAIT_FOR_DELIVERY).event(PACKAGE_NOT_DELIVERED)
                    .action(actions.reassignCourier())
                    .and()
                .withExternal()
                    .source(WAIT_FOR_DELIVERY).target(DELIVERED).event(PACKAGE_DELIVERED)
                    .action(actions.notifyOfDeliveredPackage())
                    .and()
                .withExternal()
                    .source(DELIVERED).target(USER_ACTION_IS_FOR_REFUND).event(CLAIM_UPDATED)
                    .and()
                .withExternal()
                    .source(USER_ACTION_IS_FOR_REFUND).target(USER_ACTION_IS_FOR_REFUND_RESULT).event(IS_FOR_REFUND_COMPLETED)
                    .and()
                .withChoice()
                    .source(USER_ACTION_IS_FOR_REFUND_RESULT)
                    .first(REFUND_INITIATED, guards.isForRefund(), actions.initiateRefund())
                    .then(ITEM_SERVICING_COMPLETED, guards.isNotForRefund())
                    .last(ITEM_SERVICING_COMPLETED)
                    .and()
                .withInternal()
                    .source(REFUND_INITIATED)
                    .action(actions.checkIfRefunded())
                    .timer(REFUND_TIMEOUT_PERIOD)
                    .and()
                .withExternal()
                    .source(REFUND_INITIATED)
                    .guard(guards.isRefundCompleted())
                    .target(ITEM_SERVICING_COMPLETED)
                    .event(REFUND_COMPLETED)
                    .and()
                .withExternal()
                    .source(REFUND_INITIATED).target(REFUND_INITIATED).event(REFUND_COMPLETED)
                    .guard(guards.isRefundNotCompleted());
    }
}
