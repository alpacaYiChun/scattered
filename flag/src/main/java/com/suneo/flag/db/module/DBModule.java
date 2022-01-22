package com.suneo.flag.db.module;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;

public class DBModule extends AbstractModule {
	@Provides
    public DynamoDBMapper getDynamodbMapper() {
		AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();
		return new DynamoDBMapper(client);
    }
	
	@Override
	public void configure() {
		
	}
}
