package com.saga.orchestrator.infra.repository;

import com.saga.orchestrator.domain.model.WorkflowProcess;
import com.saga.orchestrator.domain.model.enums.WorkflowState;
import com.saga.orchestrator.domain.out.WorkflowRepositoryApi;
import com.saga.orchestrator.infra.mapper.ProcessEntityMapper;
import com.saga.orchestrator.infra.repository.jpa.ProcessEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class WorkflowRepository implements WorkflowRepositoryApi {

    private final ProcessEntityRepository processEntityRepository;
    private final ProcessEntityMapper processEntityMapper;

    @Override
    public Optional<WorkflowProcess> findByBusinessKey(String businessKey) {
        return processEntityRepository.findByBusinessKey(businessKey)
                .map(processEntityMapper::toDomain);
    }

    @Override
    public WorkflowProcess upsert(WorkflowProcess workflowProcess) {
        return processEntityMapper.toDomain(
                processEntityRepository.save(processEntityMapper.toEntity(workflowProcess)));
    }

    @Transactional
    @Override
    public void updateState(UUID workflowId, WorkflowState state) {
        processEntityRepository.updateState(workflowId, state);
    }

}
