package com.saga.orchestrator.infra.repository.jpa;

import com.saga.orchestrator.domain.model.enums.WorkflowState;
import com.saga.orchestrator.infra.model.ProcessEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface ProcessEntityRepository extends JpaRepository<ProcessEntity, Long> {

    Optional<ProcessEntity> findByBusinessKey(String businessKey);

    @Modifying
    @Query(value = """
                    UPDATE Process
                    SET state = :state
                    WHERE workflowId = :workflowId
            """)
    void updateState(@Param("workflowId") UUID workflowId, @Param("state") WorkflowState state);
}
