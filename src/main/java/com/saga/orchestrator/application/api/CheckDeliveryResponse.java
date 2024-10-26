package com.saga.orchestrator.application.api;

import lombok.*;

@RequiredArgsConstructor
@Getter
@Setter
@Builder
public class CheckDeliveryResponse{
    private final String businessKey;
    private final String processId;
    private final DeliveredPackageResponse response;


    @AllArgsConstructor
    @Getter
    @Setter
    public static class DeliveredPackageResponse{
        private String packageId;
        private Boolean isDelivered;
    }
}

