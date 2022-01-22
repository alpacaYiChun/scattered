package com.suneo.flag.db.dao;

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
@DynamoDBTable(tableName="User")
public class UserDAO {
	@DynamoDBHashKey(attributeName = "UserId")
    private String userId;
	
	@DynamoDBAttribute(attributeName = "Name")
    private String name;
	
	@DynamoDBAttribute(attributeName = "Description")
    private String description;
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("UserId = ").append(userId)
		.append("Name = ").append(name)
		.append("Description = ").append(description);
		return sb.toString();
	}
}
