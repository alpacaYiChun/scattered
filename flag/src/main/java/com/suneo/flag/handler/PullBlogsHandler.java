package com.suneo.flag.handler;

import com.suneo.flag.cache.bean.RedisOperation;
import com.suneo.flag.db.operation.DynamodbOperation;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

public class PullBlogsHandler implements Handler{
    @Autowired
    private DynamodbOperation dynamodbOperation;

    @Autowired
    private RedisOperation redisOperation;

    @Override
    public Map<String, Object> handle(Map<String, Object> params) {
        return null;
    }
}
