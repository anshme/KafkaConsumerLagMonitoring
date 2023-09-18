package org.simplethoughts;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.ListConsumerGroupOffsetsResult;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.*;
import java.util.concurrent.ExecutionException;

public class LagCalculator {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        LagCalculator lg = new LagCalculator();
        System.out.println(lg.analyzeLag("test2","localhost:9092"));
    }
    public Map<TopicPartition, Long> analyzeLag(String groupId, String bootstrapServerConfig) throws ExecutionException, InterruptedException {
        Map<TopicPartition, Long> consumerGrpOffsets = getConsumerGrpOffsets(groupId,bootstrapServerConfig);
        Map<TopicPartition, Long> producerOffsets = getProducerOffsets(consumerGrpOffsets,bootstrapServerConfig);
        Map<TopicPartition, Long> lags = computeLags(consumerGrpOffsets, producerOffsets);
        return lags;
    }

    private AdminClient getAdminClient(String bootstrapServerConfig) {
        Properties config = new Properties();
        config.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServerConfig);
        return AdminClient.create(config);
    }

    private Map<TopicPartition, Long> getConsumerGrpOffsets(String groupId,String bootstrapServerConfig) throws ExecutionException, InterruptedException {
        AdminClient adminClient = getAdminClient(bootstrapServerConfig);
        ListConsumerGroupOffsetsResult info = adminClient.listConsumerGroupOffsets(groupId);
        Map<TopicPartition, OffsetAndMetadata> topicPartitionOffsetAndMetadataMap = info.partitionsToOffsetAndMetadata().get();

        Map<TopicPartition, Long> groupOffset = new HashMap<>();
        for (Map.Entry<TopicPartition, OffsetAndMetadata> entry : topicPartitionOffsetAndMetadataMap.entrySet()) {
            TopicPartition key = entry.getKey();
            OffsetAndMetadata metadata = entry.getValue();
            groupOffset.putIfAbsent(new TopicPartition(key.topic(), key.partition()), metadata.offset());
        }
        return groupOffset;
    }

    private KafkaConsumer<String, String> getKafkaConsumer(String bootstrapServerConfig) {
        Properties properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServerConfig);
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        return new KafkaConsumer<>(properties);
    }

    private Map<TopicPartition, Long> getProducerOffsets(Map<TopicPartition, Long> consumerGrpOffset,String bootstrapServerConfig) {
        KafkaConsumer<String, String> kafkaConsumer = getKafkaConsumer(bootstrapServerConfig);
        List<TopicPartition> topicPartitions = new LinkedList<>();
        for (Map.Entry<TopicPartition, Long> entry : consumerGrpOffset.entrySet()) {
            TopicPartition key = entry.getKey();
            topicPartitions.add(new TopicPartition(key.topic(), key.partition()));
        }
        return kafkaConsumer.endOffsets(topicPartitions);
    }

    private Map<TopicPartition, Long> computeLags(Map<TopicPartition, Long> consumerGrpOffsets, Map<TopicPartition, Long> producerOffsets) {
        Map<TopicPartition, Long> lags = new HashMap<>();
        for (Map.Entry<TopicPartition, Long> entry : consumerGrpOffsets.entrySet()) {
            Long producerOffset = producerOffsets.get(entry.getKey());
            Long consumerOffset = consumerGrpOffsets.get(entry.getKey());
            long lag = Math.abs(producerOffset - consumerOffset);
            lags.putIfAbsent(entry.getKey(), lag);
        }
        return lags;
    }
}