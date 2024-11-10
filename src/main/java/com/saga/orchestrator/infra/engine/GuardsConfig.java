package com.saga.orchestrator.infra.engine;

import com.saga.orchestrator.domain.model.CheckDeliveryProcess;
import com.saga.orchestrator.domain.model.ItemRefundProcess;
import com.saga.orchestrator.domain.model.ItemServicingProcess;
import com.saga.orchestrator.domain.model.enums.WorkflowEvent;
import com.saga.orchestrator.domain.model.enums.WorkflowState;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.statemachine.guard.Guard;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class GuardsConfig {

    @Bean
    public Guard<WorkflowState, WorkflowEvent> doNotReturnToWarehouse() {
        return context -> {
            Object data = context.getMessageHeader("data");
            if (data == null) {
                log.error("Can't create shipment");
                // todo throw an error
            }
            if (data instanceof ItemServicingProcess itemServicingProcess) {
                return !itemServicingProcess.getClaim().getShipmentInitiated();
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
            if (data instanceof ItemServicingProcess itemServicingProcess) {
                return itemServicingProcess.getClaim().getShipmentInitiated();
            }
            return false;
        };
    }

    @Bean
    public Guard<WorkflowState, WorkflowEvent> isForRefund() {
        return context -> {
            Object data = context.getMessageHeader("data");
            if (data == null) {
                log.error("Can't start refund");
                // todo throw an error
            }
            if (data instanceof ItemRefundProcess process) {
                return process.isForRefund();
            }
            return false;
        };
    }

    @Bean
    public Guard<WorkflowState, WorkflowEvent> isNotForRefund() {
        return context -> {
            Object data = context.getMessageHeader("data");
            if (data == null) {
                log.error("Can't exit state machine");
                // todo throw an error
            }
            if (data instanceof ItemRefundProcess process) {
                return !process.isForRefund();
            }
            return false;
        };
    }
}
