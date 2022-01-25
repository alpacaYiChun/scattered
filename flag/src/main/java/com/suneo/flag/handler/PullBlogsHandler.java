package com.suneo.flag.handler;

import com.suneo.flag.cache.bean.RedisOperation;
import com.suneo.flag.db.dao.FollowDAO;
import com.suneo.flag.db.operation.DynamodbOperation;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

public class PullBlogsHandler implements Handler{
    private static final int INTERVAL_MILLSECONDS = 60*1000;

    @Autowired
    private DynamodbOperation dynamodbOperation;

    @Autowired
    private RedisOperation redisOperation;

    @Override
    public Map<String, Object> handle(Map<String, Object> params) {
        long current = System.currentTimeMillis();
        long cutoff = (Long)params.get(Constants.CUTOFF_TIMESTAMP);

        long currentSlotIndex = current / INTERVAL_MILLSECONDS;
        long cutoffSlotIndex = cutoff / INTERVAL_MILLSECONDS;

        String user = String.valueOf(params.get(Constants.USER_NAME));

        try {
            List<FollowDAO> following = dynamodbOperation.queryFollowsByUser(user);
            following.stream().forEach(f -> {
                String friend = f.getFollowing();
                long min = Long.MAX_VALUE;
                long max = Long.MIN_VALUE;
                for (long i = cutoffSlotIndex; i <= currentSlotIndex; i++) {
                    String cacheKey = friend + "_" + i;
                    if(redisOperation.exists(cacheKey)) {

                    } else {
                        min = Math.min(min, i);
                        max = Math.max(max, i);
                    }
                }

                if(max >= min) {
                    
                }
            });
        } catch (Exception e) {

        }


        return null;
    }

    @Override
    public boolean valid(Map<String, Object> params) {
        return params.containsKey(Constants.USER_NAME)
                && params.containsKey(Constants.CUTOFF_TIMESTAMP);
    }
}
