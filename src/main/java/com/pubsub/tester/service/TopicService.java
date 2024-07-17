package com.pubsub.tester.service;

import com.google.api.core.ApiFuture;
import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.rpc.AlreadyExistsException;
import com.google.api.gax.rpc.TransportChannelProvider;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.cloud.pubsub.v1.TopicAdminClient;
import com.google.cloud.pubsub.v1.TopicAdminSettings;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.ProjectTopicName;
import com.google.pubsub.v1.PubsubMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class TopicService {

    private final String projectId;
    private final TransportChannelProvider channelProvider;
    private final CredentialsProvider credentialsProvider;

    public TopicService(@Value("${spring.cloud.gcp.project-id}") final String projectId,
                        @Qualifier("CHANNEL_PROVIDER") final TransportChannelProvider channelProvider,
                        @Qualifier("CREDENTIALS_PROVIDER") final CredentialsProvider credentialsProvider) {
        this.projectId = projectId;
        this.channelProvider = channelProvider;
        this.credentialsProvider = credentialsProvider;
    }

    public void create(final String topicName) {
        try {
            TopicAdminSettings topicAdminSettings = buildTopicAdminSettings();

            TopicAdminClient topicAdminClient = TopicAdminClient.create(topicAdminSettings);

            topicAdminClient.createTopic(ProjectTopicName.of(projectId, topicName));
        } catch (AlreadyExistsException e) {
            log.warn("Topic named {} already exists.", topicName);
        } catch (Exception e) {
            log.error("Error trying create topic {}", topicName, e);
            throw new RuntimeException("Cannot create topic.", e);
        }
    }

    private TopicAdminSettings buildTopicAdminSettings() throws IOException {
        return TopicAdminSettings.newBuilder()
                .setTransportChannelProvider(channelProvider)
                .setCredentialsProvider(credentialsProvider)
                .build();
    }

    public String publishMessage(final String topicName, final String message) throws InterruptedException {
        Publisher publisher = null;

        try {
            publisher = buildPublisher(topicName);

            PubsubMessage pubsubMessage = buildMessage(message);

            String messageId = publishMessage(publisher, pubsubMessage);
            log.info("Published message ID {} with message {}", messageId, pubsubMessage);
            return messageId;
        } catch (IOException e) {
            log.error("Cannot create publisher", e);
            throw new RuntimeException("Cannot create publisher.", e);
        } finally {
            if (publisher != null) {
                publisher.shutdown();
                publisher.awaitTermination(1, TimeUnit.MINUTES);
            }
        }
    }

    private String publishMessage(Publisher publisher, PubsubMessage pubsubMessage) {
        try {
            ApiFuture<String> messageIdFuture = publisher.publish(pubsubMessage);
            return messageIdFuture.get();
        } catch (ExecutionException | InterruptedException e) {
            log.error("Error trying publish message", e);
            throw new RuntimeException("Error trying publish message.", e);
        }
    }

    private PubsubMessage buildMessage(final String message) {
        ByteString data = ByteString.copyFromUtf8(message);
        return PubsubMessage.newBuilder()
                .setData(data)
                .build();
    }

    private Publisher buildPublisher(final String topicName) throws IOException {
        return Publisher.newBuilder(ProjectTopicName.of(projectId, topicName))
                .setChannelProvider(channelProvider)
                .setCredentialsProvider(credentialsProvider)
                .build();
    }
}
