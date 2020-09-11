package com.brightfield.streams;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.KafkaContainer;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class InfrastructureTestResource implements QuarkusTestResourceLifecycleManager {

    private final Logger log = LoggerFactory.getLogger(InfrastructureTestResource.class);
    private final KafkaContainer kafkaContainer = new KafkaContainer("5.5.1");

    @Override
    public int order() {
        return 1;
    }

    @Override
    public void init(Map<String, String> initArgs) {
        log.info("Initialising...");
    }

    @Override
    public Map<String, String> start() {
        log.info("Starting kafka test container...");
        this.kafkaContainer.start();

        log.info("Creating topic...");
        createTopics("chat-messages");
        return configurationParameters();
    }

    @Override
    public void stop() {
        this.kafkaContainer.close();
    }

    private void createTopics(String... topics) {
        var newTopics =
                Arrays.stream(topics)
                        .map(topic -> new NewTopic(topic, 1, (short) 1))
                        .collect(Collectors.toList());
        try (var admin = AdminClient.create(Map.of("bootstrap.servers", getKafkaBrokers()))) {
            admin.createTopics(newTopics);
        }
    }

    private String getKafkaBrokers() {
        this.kafkaContainer.getFirstMappedPort();
        return String.format("%s:%d", kafkaContainer.getContainerIpAddress(), kafkaContainer.getMappedPort(KafkaContainer.KAFKA_PORT));
    }

    private Map<String, String> configurationParameters() {
        log.info("Returning configurationParameters...");
        final Map<String, String> conf = new HashMap<>();
        String bootstrapServers = getKafkaBrokers();
        log.info("Brokers: {}", bootstrapServers);
        conf.put("bootstrap.servers", kafkaContainer.getBootstrapServers());
        conf.put("quarkus.kafka-streams.bootstrap-servers", bootstrapServers);
        conf.put("mp.messaging.outgoing.delivery.bootstrap.servers", bootstrapServers);
        return conf;
    }
}

