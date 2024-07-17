package com.pubsub.demo.config;

import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.NoCredentialsProvider;
import com.google.api.gax.grpc.GrpcTransportChannel;
import com.google.api.gax.rpc.FixedTransportChannelProvider;
import com.google.api.gax.rpc.TransportChannelProvider;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PubSubConfig {

    @Value("${spring.cloud.gcp.pubsub.emulator-host}")
    private String emulatorHostPort;

    @Bean(name = "CHANNEL")
    public ManagedChannel initializeChannel() {
        return ManagedChannelBuilder.forTarget(emulatorHostPort).usePlaintext().build();
    }

    @Bean(name = "CHANNEL_PROVIDER")
    public TransportChannelProvider initializeFixedTransportChannelProvider(final ManagedChannel channel) {
        return FixedTransportChannelProvider.create(GrpcTransportChannel.create(channel));
    }

    @Bean(name = "CREDENTIALS_PROVIDER")
    public CredentialsProvider initializeCredentialsProvider() {
        return NoCredentialsProvider.create();
    }

}
