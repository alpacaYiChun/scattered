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
                long min = Long.MAX_VALUE;
                long max = Long.MIN_VALUE;
                for (long i = startSlotIndex; i <= endSlotIndex; i++) {
                    String cacheKey = friend + "_" + i;
                    if(redisOperation.exists(cacheKey)) {
                    	List<byte[]> rawList = redisOperation.getBinaryList(cacheKey.getBytes());
                        List<PostDAO> postList = rawList.stream()
                        .map(e -> {
							try {
								return SerializationUtil.deserialize(e, PostDAO.class);
							} catch (ClassNotFoundException e1) {
								return null;
							} catch (IOException e1) {
								return null;
							}
						})
                        .filter(e -> e!=null)
                        .sorted((e1, e2) -> compLong(e1.getTimestamp(),e2.getTimestamp()))
                        .collect(Collectors.toList());
                        posts.add(postList);
                    } else {
                        min = Math.min(min, i);
                        max = Math.max(max, i);                        
                    }
                }

                if(max >= min) {
                    long from = min * CacheConstants.INTERVAL_MILLSECONDS;
                    long to = max * CacheConstants.INTERVAL_MILLSECONDS;
                    List<PostDAO> cacheMissing = dynamodbOperation.queryPostsByUserAndTimeInterval(friend, from, to);
                    cacheMissing.sort((e1, e2) -> compLong(e1.getTimestamp(), e2.getTimestamp()));
                    posts.add(cacheMissing);       
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
