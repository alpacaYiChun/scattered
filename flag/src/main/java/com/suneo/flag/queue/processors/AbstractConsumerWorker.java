package com.suneo.flag.queue.processors;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class AbstractConsumerWorker<K, V> {
    protected final KafkaConsumer<K, V> kafkaConsumer;

    public AbstractConsumerWorker(KafkaConsumer<K, V> kafkaConsumer) {
        this.kafkaConsumer = kafkaConsumer;
    }

    public void doWork() {
        while(true) {
            ConsumerRecords records = kafkaConsumer.poll(1000);
            if(!records.isEmpty()) {
                List<ConsumerRecord> listOfRecord = new ArrayList<>(records.count());
                Iterator<ConsumerRecord> iter = records.iterator();
                while(iter.hasNext()) {
                    listOfRecord.add(iter.next());
                }
                listOfRecord.parallelStream().forEach(r -> {
                    try {
                        int status = process(r);
                        if(status != 200) {
                            handleError(r, status, null);
                        }
                    } catch (Exception e) {
                        handleError(r, -1, e);
                    }
                });
            }
        }
    }

    public abstract int process(ConsumerRecord record);
    public abstract void handleError(ConsumerRecord record, int code, Throwable e);
}
