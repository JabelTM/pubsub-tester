package com.pubsub.demo.config;

import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.api.gax.core.NoCredentialsProvider;
import com.google.api.gax.grpc.GrpcTransportChannel;
import com.google.api.gax.rpc.FixedTransportChannelProvider;
import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.pubsub.v1.*;
import com.google.pubsub.v1.ProjectSubscriptionName;
import com.google.pubsub.v1.ProjectTopicName;
import com.google.pubsub.v1.PushConfig;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class PubSubConfig {

    @Value("${spring.cloud.gcp.project-id}")
    private String projectId;

    @Value("${spring.cloud.gcp.pubsub.emulator-host}")
    private String emulatorHostPort;

    @Bean
    public void initializePubSub() throws IOException {
        ManagedChannel channel = ManagedChannelBuilder.forTarget(emulatorHostPort).usePlaintext().build();
        FixedTransportChannelProvider channelProvider = FixedTransportChannelProvider.create(GrpcTransportChannel.create(channel));
        CredentialsProvider noCredentials = NoCredentialsProvider.create();

        TopicAdminSettings topicAdminSettings = TopicAdminSettings.newBuilder()
                .setTransportChannelProvider(channelProvider)
                .setCredentialsProvider(noCredentials)
                .build();

        SubscriptionAdminSettings subscriptionAdminSettings = SubscriptionAdminSettings.newBuilder()
                .setTransportChannelProvider(channelProvider)
                .setCredentialsProvider(noCredentials)
                .build();

        try (TopicAdminClient topicAdminClient = TopicAdminClient.create(topicAdminSettings);
             SubscriptionAdminClient subscriptionAdminClient = SubscriptionAdminClient.create(subscriptionAdminSettings)) {

            ProjectTopicName topicName = ProjectTopicName.of(projectId, "topic-teste");
            ProjectSubscriptionName subscriptionName = ProjectSubscriptionName.of(projectId, "teste-sub");

            // Cria o tópico se não existir
            try {
                topicAdminClient.createTopic(topicName);
            } catch (Exception e) {
                System.out.println("Tópico já existe ou houve um erro ao criá-lo: " + e.getMessage());
            }

            // Cria a subscrição se não existir
            try {
                subscriptionAdminClient.createSubscription(
                        subscriptionName,
                        topicName,
                        PushConfig.getDefaultInstance(),
                        10);
            } catch (Exception e) {
                System.out.println("Subscrição já existe ou houve um erro ao criá-la: " + e.getMessage());
            }
        }
    }

    @Bean
    public Publisher publisher() throws IOException {
        ManagedChannel channel = ManagedChannelBuilder.forTarget(emulatorHostPort).usePlaintext().build();
        FixedTransportChannelProvider channelProvider = FixedTransportChannelProvider.create(GrpcTransportChannel.create(channel));
        Credentials noCredentials = GoogleCredentials.create(null);

        return Publisher.newBuilder(ProjectTopicName.of(projectId, "topic-teste"))
                .setChannelProvider(channelProvider)
                .setCredentialsProvider(FixedCredentialsProvider.create(noCredentials))
                .build();
    }
}
