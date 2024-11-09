package com.saga.orchestrator.application.api;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class CheckDeliveryResponse{
    private String businessKey;
    private String processId;
    private DeliveredPackageResponse response;


    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class DeliveredPackageResponse{
        private String packageId;
        private Boolean isDelivered;
    }
}

