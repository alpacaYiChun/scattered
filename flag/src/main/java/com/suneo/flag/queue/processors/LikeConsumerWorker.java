package com.suneo.flag.queue.processors;

import com.suneo.flag.cache.RedisOperation;
import com.suneo.flag.db.operation.DynamodbOperation;
import com.suneo.flag.queue.module.KafkaModule;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

public class LikeConsumerWorker extends AbstractConsumerWorker<String, String> implements Runnable{
    private final DynamodbOperation dynamodbOperation;
    private final RedisOperation redisOperation;

    public LikeConsumerWorker(KafkaConsumer<String, String> kafkaConsumer,
                              DynamodbOperation dynamodbOperation,
                              RedisOperation redisOperation) {
        super(kafkaConsumer);
        this.dynamodbOperation = dynamodbOperation;
        this.redisOperation = redisOperation;
    }

    @Override
    public int process(ConsumerRecord record) {

        System.out.println(String.format("Key=%s, Topic=%s, Partition=%d, Msg=%s",
                record.key(),
                record.topic(),
                record.partition(),
                record.value()));
        return 0;
    }

    @Override
    public void handleError(ConsumerRecord record, int code, Throwable e) {

    }

    @Override
    public void run() {
        this.doWork();
    }

    public static void main(String[] args) throws InterruptedException {
        KafkaProducer<String, String> producer = new KafkaModule().likeProducer();
        KafkaConsumer<String, String> consumer = new KafkaModule().likeConsumer();
        Runnable t = new Runnable() {
            @Override
            public void run() {
                for(int i=0;i<10000;i++) {
                    producer.send(new ProducerRecord<String, String>("like", i+"", "wocao"));
                }
            }
        };
        LikeConsumerWorker sideCar = new LikeConsumerWorker(consumer, null, null);
        Thread p = new Thread(t);
        Thread c = new Thread(sideCar);
        p.start();
        c.start();
        p.join();
        c.join();
    }
}
