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
@DynamoDBTable(tableName = "Follow")
public class FollowDAO {
	@DynamoDBHashKey(attributeName = "UserId")
    private String userId;
	
	@DynamoDBAttribute(attributeName = "Following")
    private String following;
    	
	@Override
	public String toString() {
	    StringBuilder sb = new StringBuilder();
	    sb.append("User ").append(userId).append(" follows ").append(following);
	    return sb.toString();
	}
}
