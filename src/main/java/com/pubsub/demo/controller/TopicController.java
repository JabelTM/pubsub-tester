package com.pubsub.demo.controller;

import com.pubsub.demo.domain.Message;
import com.pubsub.demo.domain.Resource;
import com.pubsub.demo.service.TopicService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/topic")
@RequiredArgsConstructor
@Slf4j
public class TopicController {

    private final TopicService topicService;

    @PostMapping("/publish/{topicName}")
    public ResponseEntity<Object> publish(@PathVariable final String topicName, @RequestBody final String message) throws InterruptedException {
        log.info("Received publishment to topic {}: {}", topicName, message);
        return ResponseEntity.ok()
                .body(topicService.publishMessage(topicName, message));
    }

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody final Resource resource) {
        topicService.create(resource.getName());
        return ResponseEntity.created(null).build();
    }

}
