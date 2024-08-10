package com.saga.orchestrator.infra.repository.jpa;

import com.saga.orchestrator.domain.model.enums.WorkflowState;
import com.saga.orchestrator.infra.model.ProcessEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface ProcessEntityRepository extends JpaRepository<ProcessEntity, Long> {

    ProcessEntity findByWorkflowId(UUID workflowId);

    Optional<ProcessEntity> findByProcessId(String processId);

    @Query("""
                    UPDATE Process p
                    SET p.state = :state
                    WHERE p.workflowId = :workflowId
            """)
    void updateState(@Param("workflowId") UUID workflowId, @Param("state") WorkflowState state);
}
