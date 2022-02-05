package com.suneo.flag.db.dao;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamoDBTable(tableName = "LikeCountLambdaMode")
public class LikeCountDAO {
    @DynamoDBHashKey(attributeName = "PostId")
    private String postId;

    @DynamoDBAttribute(attributeName = "LikeCount")
    private int likeCount;
}
