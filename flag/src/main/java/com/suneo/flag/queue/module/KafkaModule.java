package com.suneo.flag.queue.module;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

public class KafkaModule extends AbstractModule {
    @Override
    public void configure() {

    }

    @Provides
    @Singleton
    public KafkaProducer<String, String> likeProducer() {
        String address = System.getenv("ALPACA_ADDR");
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, address);
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringSerializer");
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringSerializer");
        return new KafkaProducer<>(properties);
    }

    @Provides
    @Singleton
    public List<KafkaConsumer<String, String>> likeConsumer() {
        String address = System.getenv("ALPACA_ADDR");
        String topic = System.getenv("ALPACA_TOPIC");
        Properties properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, address);
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringDeserializer");
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringDeserializer");
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, "likeGroup");
        

        int consumerNum = Integer.parseInt(System.getenv("PARTITION_NUM"));
        List<KafkaConsumer<String, String>> ret = new ArrayList<>(consumerNum);
        for (int i = 0; i < consumerNum; i++) {
        	KafkaConsumer<String, String> consumer = new KafkaConsumer<>(properties);
        	consumer.subscribe(Collections.singletonList(topic));
        	ret.add(consumer);
        }
        
        return ret;
    }
}
