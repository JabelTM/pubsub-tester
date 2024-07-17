package com.pubsub.demo.service;

import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.api.gax.core.NoCredentialsProvider;
import com.google.api.gax.grpc.GrpcTransportChannel;
import com.google.api.gax.rpc.FixedTransportChannelProvider;
import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.pubsub.v1.ProjectSubscriptionName;
import com.google.pubsub.v1.PubsubMessage;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class PubSubSubscriberService {

    @Value("${spring.cloud.gcp.project-id}")
    private String projectId;

    @Value("${spring.cloud.gcp.pubsub.emulator-host}")
    private String emulatorHostPort;

    public void receiveMessages() throws IOException {
        ManagedChannel channel = ManagedChannelBuilder.forTarget(emulatorHostPort).usePlaintext().build();
        FixedTransportChannelProvider channelProvider = FixedTransportChannelProvider.create(GrpcTransportChannel.create(channel));
        CredentialsProvider noCredentials = NoCredentialsProvider.create();

        ProjectSubscriptionName subscriptionName = ProjectSubscriptionName.of(projectId, "teste-sub");

        Subscriber subscriber = Subscriber.newBuilder(subscriptionName, (PubsubMessage message, AckReplyConsumer consumer) -> {
            System.out.println("Mensagem recebida: " + message.getData().toStringUtf8());
            consumer.ack();
        }).setChannelProvider(channelProvider)
          .setCredentialsProvider(noCredentials)
          .build();

        subscriber.startAsync().awaitRunning();

        // Aguarde um pouco para receber mensagens
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        subscriber.stopAsync().awaitTerminated();
    }
}
