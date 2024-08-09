package com.saga.orchestrator.infra.config;

import com.saga.orchestrator.domain.in.ItemServicingActionApi;
import com.saga.orchestrator.domain.in.ItemServicingApi;
import com.saga.orchestrator.domain.out.WorkflowProducerApi;
import com.saga.orchestrator.domain.out.WorkflowRepositoryApi;
import com.saga.orchestrator.domain.out.WorkflowServiceApi;
import com.saga.orchestrator.domain.service.ItemServicingActions;
import com.saga.orchestrator.domain.service.ItemServicingService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceBeanConfig {

    @Bean
    public ItemServicingApi itemServicingApi(
            WorkflowRepositoryApi workflowRepositoryApi,
            WorkflowServiceApi workflowServiceApi) {
        return new ItemServicingService(workflowRepositoryApi, workflowServiceApi);
    }

    @Bean
    public ItemServicingActionApi itemServicingActionApi(WorkflowProducerApi workflowProducerApi) {
        return new ItemServicingActions(workflowProducerApi);
    }
}
