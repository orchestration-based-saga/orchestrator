package com.saga.orchestrator.infra.engine;

import com.saga.orchestrator.domain.in.ItemServicingActionApi;
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

    private final ItemServicingActionApi itemServicingActionApi;
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
                .action(updateClaim()).action(assignCourierToShipment()).action(addRequestToContext())
                .and()
                .withInternal()
                .source(ASSIGN_COURIER)
                .and()
                .withExternal()
                .source(ASSIGN_COURIER).target(ASSIGN_COURIER_COMPLETED).event(COURIER_ASSIGNED)
                .action(notifyWarehouseOfIncomingDelivery())
                .and()
                .withInternal()
                .source(ASSIGN_COURIER_COMPLETED)
                .timer(DELIVERY_TIMEOUT_PERIOD)
                // if not reassign courier, else update status on courier and shipment
                .action(checkIfDelivered())
                .and()
                .withExternal()
                .source(ASSIGN_COURIER_COMPLETED).target(NOT_DELIVERED).event(PACKAGE_NOT_DELIVERED)
                .action(reassignCourier())
                .and()
                .withExternal()
                .source(ASSIGN_COURIER_COMPLETED).target(DELIVERED).event(PACKAGE_DELIVERED)
//                .action(updateShipment())
        // when shipment, courier and claim are updated transition to is for refund state
        ;
    }

    // ACTIONS

    @Bean
    Action<WorkflowState, WorkflowEvent> notifyWarehouseOfIncomingDelivery() {
        return context -> {
            Object data = context.getMessageHeader("data");
            UUID workflowId = (UUID) context.getMessageHeader("workflowId");
            if (data == null) {
                log.error("Can't notify warehouse of incoming package");
                // todo throw an error
            }
            if (data instanceof ShipmentProcess process) {
                context.getExtendedState().getVariables().put("packageId", process.getShipment().packageId());
                context.getExtendedState().getVariables().put("businessKey", process.getBusinessKey());
                itemServicingActionApi.notifyWarehouse(process, workflowId);
            }
        };
    }

    @Bean
    Action<WorkflowState, WorkflowEvent> checkIfDelivered() {
        return context -> {
            String packageId = (String) context.getExtendedState().getVariables().get("packageId");
            String businessKey = (String) context.getExtendedState().getVariables().get("businessKey");
            UUID workflowId = (UUID) context.getMessageHeader("workflowId");
            itemServicingActionApi.checkIfPackageIsDelivered(businessKey, packageId, workflowId);
        };
    }

    @Bean
    public Action<WorkflowState, WorkflowEvent> addRequestToContext() {
        return context -> {
            Object data = context.getMessageHeader("data");
            if (data == null) {
                log.error("Can't save assign courier request");
                // todo throw an error
            }
            if (data instanceof ItemServicingProcess) {
                context.getExtendedState().getVariables()
                        .getOrDefault("assignCourierRequest", null);
                context.getExtendedState().getVariables().put("assignCourierRequest", data);
            }
        };
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
    public Action<WorkflowState, WorkflowEvent> assignCourierToShipment() {
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
    public Action<WorkflowState, WorkflowEvent> reassignCourier() {
        return context -> {
            Object data = context.getExtendedState().getVariables()
                    .get("assignCourierRequest");
            UUID workflowId = (UUID) context.getMessageHeader("workflowId");
            if (data == null) {
                log.error("Can't reassign courier");
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

    // GUARDS

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
