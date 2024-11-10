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

    Optional<ProcessEntity> findByBusinessKeyAndProcessId(String businessKey, String processId);

    @Modifying
    @Query(value = """
                    UPDATE Process
                    SET state = :state
                    WHERE workflowId = :workflowId
            """)
    void updateState(@Param("workflowId") UUID workflowId, @Param("state") WorkflowState state);

    @Query(value = """
                SELECT p
                FROM Process p
                WHERE p.businessKey = :businessKey and p.parentProcessId is null
            """)
    Optional<ProcessEntity> findParentByBusinessKey(@Param("businessKey") String businessKey);
}
