package com.suneo.flag.handler;

import com.google.gson.Gson;
import com.suneo.flag.db.dao.FollowDAO;
import com.suneo.flag.db.dao.PostDAO;
import com.suneo.flag.db.operation.DynamodbOperation;
import com.suneo.flag.lib.MergeIterator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Configuration
public class PullBlogsHandler implements Handler{
    @Autowired
    private PostStorageManager postStorageManager;
    
    @Autowired
    private DynamodbOperation dynamodbOperation;

    @Override
    public Map<String, Object> handle(Map<String, Object> params) {
        String user = String.valueOf(params.get(Constants.USER_NAME));

        List<List<PostDAO>> posts = new ArrayList<>();
        
        try {
            List<FollowDAO> following = dynamodbOperation.queryFollowsByUser(user);
            following.stream().forEach(f -> {
                String friend = f.getFollowing();
                
                List<PostDAO> list = postStorageManager.queryUserTimeline(friend);
                
                posts.add(list);
            });
        } catch (Exception e) {
            return Map.of(Constants.ERR, e.getMessage());
        }

        List<PostDAO> merged = MergeIterator.merge(posts, (e1, e2) -> compLong(e1.getTimestamp(), e2.getTimestamp()));
        
        String json = new Gson().toJson(merged);
        
        return Map.of(Constants.PULLED_POSTS, json);
    }

    @Override
    public boolean valid(Map<String, Object> params) {
        return params.containsKey(Constants.USER_NAME);
    }
    
    private int compLong(long a1, long a2) {
    	return a1<a2?-1:(a2>a1?1:0);
    }
}
