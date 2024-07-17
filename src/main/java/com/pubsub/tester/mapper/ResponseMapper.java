package com.pubsub.tester.mapper;

import com.pubsub.tester.domain.SubscriptionResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ResponseMapper {

    public List<SubscriptionResponse> toSubscriptionResponseList(Map<String, String> response) {
        return response.entrySet()
                .stream()
                .map(it -> SubscriptionResponse.builder()
                        .id(it.getKey())
                        .message(it.getValue())
                        .build())
                .collect(Collectors.toList());
    }

}
