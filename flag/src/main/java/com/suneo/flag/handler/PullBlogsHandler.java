package com.suneo.flag.handler;

import com.suneo.flag.cache.RedisOperation;
import com.suneo.flag.db.dao.FollowDAO;
import com.suneo.flag.db.dao.PostDAO;
import com.suneo.flag.db.operation.DynamodbOperation;
import com.suneo.flag.lib.MergeIterator;
import com.suneo.flag.lib.SerializationUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
public class PullBlogsHandler implements Handler{
    @Autowired
    private DynamodbOperation dynamodbOperation;

    @Autowired
    private RedisOperation redisOperation;

    @Override
    public Map<String, Object> handle(Map<String, Object> params) {
        String user = String.valueOf(params.get(Constants.USER_NAME));

        List<List<PostDAO>> posts = new ArrayList<>();
        
        try {
            List<FollowDAO> following = dynamodbOperation.queryFollowsByUser(user);
            following.stream().forEach(f -> {
                String friend = f.getFollowing();
                if(redisOperation.exists(friend)) {
                	// List of PostIDs
                    List<String> ids = redisOperation.getList(friend);
                    List<PostDAO> fromCache = ids.stream().map(id -> {
                        try {
                            return dynamodbOperation.loadPost(id);
                        } catch (Exception ex) {
                            return null;
                        }
                    }).filter(e -> e != null)
                            .sorted((e1, e2) -> compLong(e1.getTimestamp(), e2.getTimestamp()))
                    .collect(Collectors.toList());
                    posts.add(fromCache);
                } else {
                	// Read DB
                    List<PostDAO> fromDB = dynamodbOperation.queryPosts(friend, Map.of()).stream()
                            .sorted((e1, e2) -> compLong(e1.getTimestamp(), e2.getTimestamp()))
                            .collect(Collectors.toList());
                    
                    // Write Cache
                    List<String> postIds = fromDB.stream()
                    		.map(post -> post.getId())
                    		.collect(Collectors.toList());
                    redisOperation.setFullList(friend, postIds.size(), postIds);
                    
                    // Collect Result
                    posts.add(fromDB);
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
        return params.containsKey(Constants.USER_NAME);
    }
    
    private int compLong(long a1, long a2) {
    	return a1<a2?-1:(a2>a1?1:0);
    }
}
