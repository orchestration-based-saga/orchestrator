package com.saga.orchestrator.infra.repository.jpa;

import com.saga.orchestrator.infra.model.ProcessEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProcessEntityRepository extends JpaRepository<ProcessEntity, Long> {

    Optional<ProcessEntity> findByProcessId(String processId);
}
