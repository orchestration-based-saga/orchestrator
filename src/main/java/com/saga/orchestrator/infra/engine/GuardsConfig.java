package com.saga.orchestrator.infra.engine;

import com.saga.orchestrator.domain.model.CheckDeliveryProcess;
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
    public Guard<WorkflowState, WorkflowEvent> isDelivered() {
        return context -> {
            Object data = context.getMessageHeader("data");
            if (data == null) {
                log.error("Can't create shipment");
                // todo throw an error
            }
            if (data instanceof CheckDeliveryProcess checkDeliveryProcess) {
                return checkDeliveryProcess.getIsDelivered().equals(true);
            }
            return false;
        };
    }

    @Bean
    public Guard<WorkflowState, WorkflowEvent> isNotDelivered() {
        return context -> {
            Object data = context.getMessageHeader("data");
            if (data == null) {
                log.error("Can't create shipment");
                // todo throw an error
            }
            if (data instanceof CheckDeliveryProcess checkDeliveryProcess) {
                return checkDeliveryProcess.getIsDelivered().equals(false);
            }
            return false;
        };
    }
}
