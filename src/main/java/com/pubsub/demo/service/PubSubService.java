package com.pubsub.demo.service;

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutureCallback;
import com.google.api.core.ApiFutures;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PubSubService {

    private final Publisher publisher;


    public void publishMessage(String payload) {
        ByteString data = ByteString.copyFromUtf8(payload);
        PubsubMessage pubsubMessage = PubsubMessage.newBuilder()
                .setData(data)
                .build();

        ApiFuture<String> messageIdFuture = publisher.publish(pubsubMessage);
        ApiFutures.addCallback(messageIdFuture, new ApiFutureCallback<String>() {
            @Override
            public void onFailure(Throwable throwable) {
                System.out.println("Erro ao publicar a mensagem: " + throwable.getMessage());
            }

            @Override
            public void onSuccess(String messageId) {
                System.out.println("Mensagem publicada com ID: " + messageId);
            }
        }, MoreExecutors.directExecutor());
    }
}
