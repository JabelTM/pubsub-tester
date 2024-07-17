package com.pubsub.demo.controller;

import com.pubsub.demo.domain.Resource;
import com.pubsub.demo.domain.SubscriptionResponse;
import com.pubsub.demo.mapper.ResponseMapper;
import com.pubsub.demo.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

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
