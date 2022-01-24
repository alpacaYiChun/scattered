package com.suneo.flag.db.module;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.suneo.flag.db.operation.DynamodbOperation;

public class DBModule extends AbstractModule {
	@Provides
	@Singleton
    public DynamoDBMapper getDynamodbMapper() {
		AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();
		return new DynamoDBMapper(client);
    }

    @Provides
	@Singleton
	public DynamodbOperation getDynamodbOperation(DynamoDBMapper mapper) {
		return new DynamodbOperation(mapper);
	}

	@Override
	public void configure() {
		
	}
}
