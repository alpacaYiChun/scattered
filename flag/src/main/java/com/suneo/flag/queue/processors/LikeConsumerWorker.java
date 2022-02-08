package com.suneo.flag.queue.processors;

import com.suneo.flag.db.dao.LikeDAO;
import com.suneo.flag.db.operation.DynamodbOperation;
import com.suneo.flag.queue.module.KafkaModule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

public class LikeConsumerWorker extends AbstractConsumerWorker<String, String> implements Runnable{
    private final DynamodbOperation dynamodbOperation;
    private final HashMap<String, HashSet<String>> aggragate = new HashMap<>();

    public LikeConsumerWorker(KafkaConsumer<String, String> kafkaConsumer,
                              DynamodbOperation dynamodbOperation) {
        super(kafkaConsumer, false);
        this.dynamodbOperation = dynamodbOperation;
    }

    @Override
    public int process(ConsumerRecord<String, String> record) {
        String postId = record.key();
        String userId = record.value();
        if(!aggragate.containsKey(postId)) {
        	aggragate.put(postId, new HashSet<String>());
        }
        aggragate.get(postId).add(userId);
        
        System.out.println(String.format("Key=%s, Topic=%s, Partition=%d, Msg=%s",
                record.key(),
                record.topic(),
                record.partition(),
                record.value()));
        return 0;
    }

    @Override
    public void handleError(ConsumerRecord<String, String> record, int code, Throwable e) {
    	//no-op
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
        LikeConsumerWorker sideCar = new LikeConsumerWorker(consumer, null);
        Thread p = new Thread(t);
        Thread c = new Thread(sideCar);
        p.start();
        c.start();
        p.join();
        c.join();
    }

	@Override
	public int postProcess() {
		List<LikeDAO> validated = new ArrayList<LikeDAO>();
		for(Map.Entry<String, HashSet<String>> e : aggragate.entrySet()) {
			String postId = e.getKey();
			for(String userId : e.getValue()) {
				if(!dynamodbOperation.existsLike(postId, userId)) {
					continue;
				}
				validated.add(new LikeDAO(postId, userId, System.currentTimeMillis()));
			}
		}
		dynamodbOperation.insertLikes(validated);
		return 0;
	}

	@Override
	public int prepare() {
		aggragate.clear();
		return 0;
	}
}
