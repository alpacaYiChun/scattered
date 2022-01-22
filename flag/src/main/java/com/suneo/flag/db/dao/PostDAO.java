package com.suneo.flag.db.dao;

import java.util.List;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamoDBTable(tableName = "Post")
public class PostDAO {	
	@DynamoDBHashKey(attributeName = "PostId")
    private String id;
	
    @DynamoDBAttribute(attributeName = "Content")
    private String content;
    
    @DynamoDBAttribute(attributeName = "TS")
    private long timestamp;
    
    @DynamoDBAttribute(attributeName = "UserId")
    private String userId;
    
    @DynamoDBAttribute(attributeName = "Tags")
    private List<String> tags;
    	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Id=").append(id).append("\n")
		.append("TimeStamp=").append(timestamp).append("\n")
		.append("Content=").append(content).append("\n")
		.append("UserId=").append(userId);
		
		return sb.toString();
	}
}
