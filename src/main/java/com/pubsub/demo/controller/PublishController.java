package com.pubsub.demo.controller;

import com.pubsub.demo.domain.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/publish")
@Slf4j
public class PublishController {

    @PostMapping
    public ResponseEntity<Void> publish(@RequestBody Message message) {
        log.info("Received publishment: {}", message);
        return ResponseEntity.ok().build();
    }

}
