package com.pubsub.demo.controller;

import com.pubsub.demo.service.PubSubSubscriberService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/receive")
@RequiredArgsConstructor
public class PubSubController {

    private final PubSubSubscriberService pubSubSubscriberService;

    @GetMapping
    public ResponseEntity<String> receiveMessages() {
        try {
            pubSubSubscriberService.receiveMessages();
            return ResponseEntity.ok("Mensagens recebidas");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro ao receber mensagens: " + e.getMessage());
        }
    }
}
