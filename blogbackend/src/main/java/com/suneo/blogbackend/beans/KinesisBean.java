package com.suneo.blogbackend.beans;

import org.springframework.context.annotation.Configuration;

import com.amazonaws.services.kinesis.AmazonKinesis;
import com.amazonaws.services.kinesis.AmazonKinesisClientBuilder;
import com.suneo.shared.clients.KinesisMessageQueue;

import org.springframework.context.annotation.Bean;

@Configuration
public class KinesisBean {
	@Bean
	public AmazonKinesis getKinesis() {
		AmazonKinesisClientBuilder builder = AmazonKinesisClientBuilder.standard()
    			.withRegion("us-west-2");
		return builder.build();
	}
	
	@Bean
	public KinesisMessageQueue getKinesisQueue(AmazonKinesis kinesis) {
		return new KinesisMessageQueue(kinesis);
	}
}
