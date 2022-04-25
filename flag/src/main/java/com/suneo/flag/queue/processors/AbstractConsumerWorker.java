package com.suneo.flag.queue.processors;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

public abstract class AbstractConsumerWorker<K, V> {
    protected final KafkaConsumer<K, V> kafkaConsumer;
    protected final Function<ConsumerRecord<K, V>, Integer> workFunc;
    protected final boolean paralell;

    public AbstractConsumerWorker(KafkaConsumer<K, V> kafkaConsumer, boolean paralell) {
        this.kafkaConsumer = kafkaConsumer;
        this.workFunc = r -> {
        	try {
                int status = process(r);
                if(status != 200) {
                    handleError(r, status, null);
                }
                return status;
            } catch (Exception e) {
                handleError(r, -1, e);
                return -1;
            }
        };
        this.paralell = paralell;
    }

    public void doWork() {
        while(true) {
        	prepare();
            ConsumerRecords<K, V> records = kafkaConsumer.poll(Duration.ofMillis(1000));
            if(!records.isEmpty()) {
                List<ConsumerRecord<K, V>> listOfRecord = new ArrayList<>(records.count());
                Iterator<ConsumerRecord<K,V>> iter = records.iterator();
                while(iter.hasNext()) {
                    listOfRecord.add(iter.next());
                }
                if(paralell) {
                	listOfRecord.parallelStream().forEach(r -> workFunc.apply(r));
                } else {
                	listOfRecord.stream().forEach(r -> workFunc.apply(r));
                }
            }
            postProcess();
        }
    }
    
    public abstract int prepare();
    public abstract int process(ConsumerRecord<K, V> record);
    public abstract int postProcess();
    public abstract void handleError(ConsumerRecord<K, V> record, int code, Throwable e);
}
