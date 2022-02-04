package com.suneo.flag.queue;

import java.nio.ByteBuffer;

import com.amazonaws.services.kinesis.AmazonKinesis;
import com.amazonaws.services.kinesis.AmazonKinesisClientBuilder;
import com.amazonaws.services.kinesis.model.PutRecordRequest;
import com.amazonaws.services.kinesis.model.PutRecordResult;

public class KinesisMessageQueue {
    private AmazonKinesis kinesis;
    
    public KinesisMessageQueue(AmazonKinesis kinesis) {
    	this.kinesis = kinesis;
    }
    
    public String sendMessage(String streamName, String partitionKey, String data) {
    	PutRecordRequest request = new PutRecordRequest();
    	request.setData(ByteBuffer.wrap(data.getBytes()));
    	request.setStreamName(streamName);
    	request.setPartitionKey(partitionKey);
    	
    	PutRecordResult result = kinesis.putRecord(request);
    	
    	return result.getSequenceNumber();
    }
    
    public static void main(String[] args) throws InterruptedException {
    	String streamName = "SuneoStream";
    	KinesisMessageQueue queue = new KinesisMessageQueue(AmazonKinesisClientBuilder.standard()
    			.withRegion("us-west-2").build());
    	
    	//String userId = "Alpaca";
    	//String content = "Fang Yang";
    	String json = "{\"UserId\":\"Alpaca\", \"Content\":\"Wo xiang fei dao tianshang qu, qu ya qu fang yang, gei wo xin ai de yang er chi shang yi duo mian hua tang\"}";
    	
    	for(int i=0;i<1000;i++) {
    		String rs = queue.sendMessage(streamName, i+"", json);
    		//Thread.sleep(1000);
    		System.out.println(rs);
    	}
    }
}
