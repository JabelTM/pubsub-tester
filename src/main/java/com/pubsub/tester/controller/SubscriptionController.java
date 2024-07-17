package com.pubsub.tester.controller;

import com.pubsub.tester.domain.Resource;
import com.pubsub.tester.domain.SubscriptionResponse;
import com.pubsub.tester.mapper.ResponseMapper;
import com.pubsub.tester.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/v1/subscription")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;
    private final ResponseMapper mapper;

    @GetMapping("/receive/{subscriptionName}")
    public ResponseEntity<List<SubscriptionResponse>> receiveMessages(@PathVariable final String subscriptionName) throws IOException {
        return ResponseEntity.ok(mapper.toSubscriptionResponseList(subscriptionService.receiveMessages(subscriptionName)));
    }

    @PostMapping("/{topicName}")
    public ResponseEntity<Void> create(@PathVariable final String topicName, @RequestBody final Resource resource) {
        subscriptionService.create(topicName, resource.getName());
        return ResponseEntity.created(null).build();
    }
}
