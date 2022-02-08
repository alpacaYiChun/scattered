package com.suneo.flag.db.dao;

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
@DynamoDBTable(tableName = "Comment")
public class CommentDAO implements Serializable {
	private static final long serialVersionUID = 1787065760497761216L;

	@EqualsAndHashCode.Include
	@DynamoDBHashKey(attributeName = "CommentId")
    private String commentId;
	
	@DynamoDBAttribute(attributeName = "Content")
    private String content;
	
	@DynamoDBAttribute(attributeName = "ParentId")
    private String parentId;
	
	@DynamoDBAttribute(attributeName = "PostId")
    private String postId;
	
	@DynamoDBAttribute(attributeName = "UserId")
    private String userId;
	
	@DynamoDBAttribute(attributeName = "TS")
    private long ts;
    	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("id=").append(commentId).append("\n")
		.append("postID=").append(postId).append("\n")
		.append("userID=").append(userId).append("\n")
		.append("parentID=").append(parentId).append("\n")
		.append("Content=").append(content).append("\n")
		.append("timestamp=").append(ts);
		return sb.toString();
	}
}
