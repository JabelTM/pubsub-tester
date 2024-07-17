package com.pubsub.demo.receiver;

import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.pubsub.v1.PubsubMessage;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Getter
@Slf4j
public class CustomMessageReceiver implements MessageReceiver {

    private final Map<String, String> messages = new HashMap<>();

    @Override
    public void receiveMessage(PubsubMessage pubsubMessage, AckReplyConsumer ackReplyConsumer) {
        String message = getSanitizedMessage(pubsubMessage);
        messages.put(pubsubMessage.getMessageId(), message);
        log.info("Mensagem recebida: {}", message);
        ackReplyConsumer.ack();
    }

    private String getSanitizedMessage(PubsubMessage pubsubMessage) {
        return pubsubMessage.getData().toStringUtf8()
                .replaceAll("\\\\r\\\\n|\\\\n", "");
    }

    public void clearMessages() {
        messages.clear();
    }

}
