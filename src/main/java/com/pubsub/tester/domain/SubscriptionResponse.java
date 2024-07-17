package com.pubsub.tester.domain;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class SubscriptionResponse {

    private final String id;
    private final String message;

}
