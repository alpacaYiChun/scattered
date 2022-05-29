package com.suneo.datamodel.db.dao;

import java.io.Serializable;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamoDBTable(tableName="User")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class UserDAO implements Serializable {
	private static final long serialVersionUID = 8821549867458892517L;

	@EqualsAndHashCode.Include
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
