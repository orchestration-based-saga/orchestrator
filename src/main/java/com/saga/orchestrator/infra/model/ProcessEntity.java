package com.saga.orchestrator.infra.model;

import com.saga.orchestrator.domain.model.enums.WorkflowState;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity(name = "Process")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProcessEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    Long parentProcessId;
    String processId;
    String businessKey;
    UUID workflowId;
    @Enumerated(EnumType.STRING)
    WorkflowState state;
}
