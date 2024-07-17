package com.pubsub.tester.service;

import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.rpc.AlreadyExistsException;
import com.google.api.gax.rpc.TransportChannelProvider;
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.cloud.pubsub.v1.SubscriptionAdminClient;
import com.google.cloud.pubsub.v1.SubscriptionAdminSettings;
import com.google.pubsub.v1.ProjectSubscriptionName;
import com.google.pubsub.v1.PushConfig;
import com.google.pubsub.v1.TopicName;
import com.pubsub.tester.receiver.CustomMessageReceiver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.nonNull;

@Service
@Slf4j
public class SubscriptionService {

    private final String projectId;
    private final TransportChannelProvider channelProvider;
    private final CredentialsProvider credentialsProvider;
    private final CustomMessageReceiver messageReceiver;

    public SubscriptionService(@Value("${spring.cloud.gcp.project-id}") final String projectId,
                               @Qualifier("CHANNEL_PROVIDER") final TransportChannelProvider channelProvider,
                               @Qualifier("CREDENTIALS_PROVIDER") final CredentialsProvider credentialsProvider,
                               final CustomMessageReceiver messageReceiver) {
        this.projectId = projectId;
        this.channelProvider = channelProvider;
        this.credentialsProvider = credentialsProvider;
        this.messageReceiver = messageReceiver;
    }

    public void create(final String topicName, final String subscriptionName) {
        try {
            SubscriptionAdminSettings subscriptionAdminSettings = guildSubscritionAdminSettings();

            SubscriptionAdminClient subscriptionAdminClient = buildSubscritionAdminClient(subscriptionAdminSettings);

            createSubscrition(topicName, subscriptionName, subscriptionAdminClient);
        } catch (AlreadyExistsException e) {
            log.warn("Subscription named {} already exists.", subscriptionName);
        } catch (IOException e) {
            log.error("Error creating SubscritionAdminSettings", e);
            throw new RuntimeException("Error creating SubscritionAdminSettings", e);
        }
    }

    private void createSubscrition(final String topicName, final String subscriptionName, final SubscriptionAdminClient subscriptionAdminClient) {
        subscriptionAdminClient.createSubscription(
                ProjectSubscriptionName.of(projectId, subscriptionName),
                TopicName.of(projectId, topicName),
                PushConfig.getDefaultInstance(),
                10);
    }

    private SubscriptionAdminClient buildSubscritionAdminClient(final SubscriptionAdminSettings subscriptionAdminSettings) throws IOException {
        return SubscriptionAdminClient.create(subscriptionAdminSettings);
    }

    private SubscriptionAdminSettings guildSubscritionAdminSettings() throws IOException {
        return SubscriptionAdminSettings.newBuilder()
                .setTransportChannelProvider(channelProvider)
                .setCredentialsProvider(credentialsProvider)
                .build();
    }

    public Map<String, String> receiveMessages(final String subscriptionName) throws IOException {
        Subscriber subscriber = null;

        try {
            subscriber = Subscriber.newBuilder(ProjectSubscriptionName.of(projectId, subscriptionName), messageReceiver)
                    .setChannelProvider(channelProvider)
                    .setCredentialsProvider(credentialsProvider)
                    .build();

            subscriber.startAsync().awaitRunning();

            Thread.sleep(3000);

            Map<String, String> messages = new HashMap<>(messageReceiver.getMessages());
            messageReceiver.clearMessages();
            return messages;
        } catch (InterruptedException e) {
            log.error("Error in thread sleep.", e);
            throw new RuntimeException(e);
        } finally {
            if (nonNull(subscriber)) {
                subscriber.stopAsync().awaitTerminated();
            }
        }
    }
}
