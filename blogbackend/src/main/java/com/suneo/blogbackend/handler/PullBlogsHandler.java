package com.suneo.blogbackend.handler;

import com.google.gson.Gson;
import com.suneo.blogbackend.Constants;
import com.suneo.blogbackend.lib.MergeIterator;
import com.suneo.datamodel.db.dao.FollowDAO;
import com.suneo.datamodel.db.dao.PostDAO;
import com.suneo.datamodel.db.operation.DynamodbOperation;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Configuration
public class PullBlogsHandler implements Handler{
	private static final Logger logger = LogManager.getLogger(PullBlogsHandler.class);
	
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
            following.forEach(f -> {
            	logger.info("{} is following {}", user, f);
                String friend = f.getFollowing();
                
                List<PostDAO> list = postStorageManager.queryUserTimeline(friend);
                
                posts.add(list);
            });
            
            List<PostDAO> merged = MergeIterator.merge(posts, (e1, e2) -> compLong(e1.getTimestamp(), e2.getTimestamp()));
            
            String json = new Gson().toJson(merged);
            
            return Map.of(Constants.PULLED_POSTS, json);
        } catch (Exception e) {
        	logger.error(ExceptionUtils.getStackTrace(e));
            return Map.of(Constants.ERR, e.getMessage());
        } 
    }

    @Override
    public boolean valid(Map<String, Object> params) {
        return params.containsKey(Constants.USER_NAME);
    }
    
    private int compLong(long a1, long a2) {
    	return Long.compare(a1,a2);
    }
}
