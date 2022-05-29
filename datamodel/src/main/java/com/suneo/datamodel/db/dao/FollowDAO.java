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
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@DynamoDBTable(tableName = "Follow")
public class FollowDAO implements Serializable {
	private static final long serialVersionUID = 4197931055173082946L;

	@EqualsAndHashCode.Include
	@DynamoDBHashKey(attributeName = "UserId")
    private String userId;
	
	@EqualsAndHashCode.Include
	@DynamoDBAttribute(attributeName = "Following")
    private String following;
    	
	@Override
	public String toString() {
	    StringBuilder sb = new StringBuilder();
	    sb.append("User ").append(userId).append(" follows ").append(following);
	    return sb.toString();
	}
}
