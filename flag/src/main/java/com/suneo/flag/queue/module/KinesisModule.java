package com.suneo.flag.queue.module;

import com.amazonaws.services.kinesis.AmazonKinesis;
import com.amazonaws.services.kinesis.AmazonKinesisClientBuilder;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.suneo.flag.queue.KinesisMessageQueue;

public class KinesisModule extends AbstractModule {
	@Provides
	@Singleton
	public AmazonKinesis getKinesis() {
		AmazonKinesisClientBuilder builder = AmazonKinesisClientBuilder.standard()
    			.withRegion("us-west-2");
		return builder.build();
	}
	
	@Provides
	@Singleton
	public KinesisMessageQueue getKinesisQueue(AmazonKinesis kinesis) {
		return new KinesisMessageQueue(kinesis);
	}
	
	@Override
	public void configure() {
		
	}
}
