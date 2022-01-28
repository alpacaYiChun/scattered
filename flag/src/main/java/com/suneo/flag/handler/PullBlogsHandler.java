package com.suneo.flag.handler;

import com.suneo.flag.cache.CacheConstants;
import com.suneo.flag.cache.RedisOperation;
import com.suneo.flag.db.dao.FollowDAO;
import com.suneo.flag.db.dao.PostDAO;
import com.suneo.flag.db.operation.DynamodbOperation;
import com.suneo.flag.lib.MergeIterator;
import com.suneo.flag.lib.SerializationUtil;

import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PullBlogsHandler implements Handler{
    @Autowired
    private DynamodbOperation dynamodbOperation;

    @Autowired
    private RedisOperation redisOperation;

    @Override
    public Map<String, Object> handle(Map<String, Object> params) {
        long end = (Long)params.get(Constants.END_TIMESTAMP);
        long start = (Long)params.get(Constants.START_TIMESTAMP);

        long startSlotIndex = start / CacheConstants.INTERVAL_MILLSECONDS;
        long endSlotIndex = end / CacheConstants.INTERVAL_MILLSECONDS;

        String user = String.valueOf(params.get(Constants.USER_NAME));

        List<List<PostDAO>> posts = new ArrayList<>();
        
        try {
            List<FollowDAO> following = dynamodbOperation.queryFollowsByUser(user);
            following.stream().forEach(f -> {
                String friend = f.getFollowing();
                if(redisOperation.exists(friend)) {
                    List<byte[]> raw = redisOperation.getList(friend.getBytes());
                    List<PostDAO> fromCache = raw.stream().map(e -> {
                        try {
                            return SerializationUtil.deserialize(e, PostDAO.class);
                        } catch (Exception ex) {
                            return null;
                        }
                    }).filter(e -> e != null)
                            .sorted((e1, e2) -> compLong(e1.getTimestamp(), e2.getTimestamp()))
                    .collect(Collectors.toList());
                    posts.add(fromCache);
                } else {
                    List<PostDAO> fromDB = dynamodbOperation.queryPosts(friend, Map.of()).stream()
                            .sorted((e1, e2) -> compLong(e1.getTimestamp(), e2.getTimestamp()))
                            .collect(Collectors.toList());
                    posts.add(fromDB);
                    List<byte[]> bytesOfPosts = fromDB.stream()
                            .map(e -> {
                                try {
                                    return SerializationUtil.serialize(e);
                                } catch (IOException ioException) {
                                    return null;
                                }
                            })
                            .collect(Collectors.toList());
                    redisOperation.setFullList(friend.getBytes(), fromDB.size(), bytesOfPosts);
                }
            });
        } catch (Exception e) {
            return Map.of(Constants.ERR, e.getMessage());
        }

        List<PostDAO> merged = MergeIterator.merge(posts, (e1, e2) -> compLong(e1.getTimestamp(), e2.getTimestamp()));
        
        return Map.of("PULLED_POSTS", merged);
    }

    @Override
    public boolean valid(Map<String, Object> params) {
        return params.containsKey(Constants.USER_NAME)
                && params.containsKey(Constants.START_TIMESTAMP)
                && params.containsKey(Constants.END_TIMESTAMP);
    }
    
    private int compLong(long a1, long a2) {
    	return a1<a2?-1:(a2>a1?1:0);
    }
}
