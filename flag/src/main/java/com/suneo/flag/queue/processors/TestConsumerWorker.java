package com.suneo.flag.queue.processors;

import com.suneo.flag.queue.module.KafkaModule;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

public class TestConsumerWorker extends AbstractConsumerWorker<String, String> {
    public TestConsumerWorker(KafkaConsumer<String, String> kafkaConsumer) {
        super(kafkaConsumer, false);
    }

    @Override
    public int prepare() {
        return 0;
    }

    @Override
    public int process(ConsumerRecord<String, String> record) {
        System.out.println(String.format("Key=%s, Topic=%s, Partition=%d, Msg=%s",
                record.key(),
                record.topic(),
                record.partition(),
                record.value()));
        return 0;
    }

    @Override
    public int postProcess() {
        return 0;
    }

    @Override
    public void handleError(ConsumerRecord<String, String> record, int code, Throwable e) {

    }

    public static void main(String[] args) throws InterruptedException {
        String mode = args[0];
        KafkaModule module = new KafkaModule();

        if(mode.equals("0")) {
            int n = Integer.parseInt(args[1]);
            Thread[] threads = new Thread[n];
            for(int i=0;i<n;i++) {
                KafkaConsumer<String, String> consumer = module.likeConsumer().get(0);
                TestConsumerWorker worker = new TestConsumerWorker(consumer);
                Thread t = new Thread (() -> worker.doWork());
                threads[i] = t;
            }
            for(int i=0;i<n;i++) {
                threads[i].start();
            }
            for(int i=0;i<n;i++) {
                threads[i].join();
            }
        } else {
            KafkaProducer<String, String> producer = module.likeProducer();
            produceTest(producer);
        }
    }

    private void consumeTest() {
        doWork();
    }

    private static void produceTest(KafkaProducer<String, String> producer) throws InterruptedException {
        for(int i=0;i<10000;i++) {
            producer.send(new ProducerRecord<String, String>("like", ""+i, "waste"));
            Thread.sleep(100);
        }
    }
}
