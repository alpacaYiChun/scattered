package com.suneo.flag.db.bean;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.suneo.flag.db.operation.DynamodbOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DBBean {
    @Bean
    public DynamoDBMapper getDynamodbMapper() {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();
        return new DynamoDBMapper(client);
    }

    @Bean
    public DynamodbOperation getDynamodbOperation(DynamoDBMapper mapper) {
        return new DynamodbOperation(mapper);
    }
}
