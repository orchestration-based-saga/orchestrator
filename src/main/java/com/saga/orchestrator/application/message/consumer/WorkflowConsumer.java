package com.saga.orchestrator.application.message.consumer;

import com.saga.orchestrator.application.api.ItemServicingProcessResponse;
import com.saga.orchestrator.application.api.WorkflowConstants;
import com.saga.orchestrator.application.api.WorkflowStartProcessMessage;
import com.saga.orchestrator.application.exception.BusinessError;
import com.saga.orchestrator.application.mapper.ProcessMapper;
import com.saga.orchestrator.domain.in.ItemServicingApi;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
public class WorkflowConsumer {
    private final ItemServicingApi itemServicingApi;
    private final ProcessMapper mapper;

    @Bean
    public Consumer<Message<WorkflowStartProcessMessage>> startWorkflowProcess() {
        return message -> {
            try {
                WorkflowStartProcessMessage payload = message.getPayload();
                if (payload.processId().equals(WorkflowConstants.ITEM_SERVICING)) {
                    itemServicingApi.itemServicing(mapper.toItemServicingProcess(payload));
                }
            } catch (BusinessError be) {
                processBusinessError(be);
            } catch (RuntimeException e) {
                processTechError(e);
            }
        };
    }

    @Bean
    public Consumer<Message<ItemServicingProcessResponse>> claimCreated() {
        return msg -> itemServicingApi.claimCreated(msg.getPayload().workflow());
    }


    private void processBusinessError(RuntimeException e) {
    }

    private void processTechError(Exception e) {
    }
}
