package com.pubsub.demo.controller;

import com.pubsub.demo.domain.Message;
import com.pubsub.demo.service.PubSubService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/publish")
@RequiredArgsConstructor
@Slf4j
public class PublishController {

    private final PubSubService pubSubService;

    @PostMapping
    public ResponseEntity<Void> publish(@RequestBody Message message) {
        log.info("Received publishment: {}", message);
        try {
            pubSubService.publishMessage(message.getPayload());
        } catch (Exception e) {
            log.error("Error trying publish message", e);
            throw new RuntimeException(e);
        }
        return ResponseEntity.ok().build();
    }

}
