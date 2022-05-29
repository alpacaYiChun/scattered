package com.suneo.flag.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import com.google.gson.Gson;
import com.suneo.flag.cache.RedisOperation;
import com.suneo.flag.db.dao.PostDAO;
import com.suneo.flag.db.operation.DynamodbOperation;

@Configuration
public class PostStorageManager {
	@Autowired
    private DynamodbOperation dynamodbOperation;

    @Autowired
    private RedisOperation redisOperation;
    
    public List<PostDAO> queryUserTimeline(String friend) {
    	List<PostDAO> posts = List.of();
    	
    	if(redisOperation.exists(friend)) {
        	// List of PostIDs
            List<String> ids = redisOperation.getList(friend);

            // List of Posts from Cache
            Map<String, String> postsFromCache = redisOperation.getMultiKeys(ids.toArray(new String[0]));

            // List of Posts from DB
            List<PostDAO> postsFromDB = ids.stream()
                    .filter(id -> !postsFromCache.containsKey(id))
                    .map(id -> {
                        try {
                            return dynamodbOperation.loadPost(id);
                        } catch (Exception ex) {
                            return null;
                        }
                    }).filter(e -> e != null).collect(Collectors.toList());

            // Write Content Cache
            Map<String, String> createMap = new HashMap<>();
            for(PostDAO post : postsFromDB) {
                createMap.put(post.getId(), toJson(post));
            }
            if(createMap.size() > 0) {
                redisOperation.putMultiKeys(createMap);
            }

            // List of everything
            List<PostDAO> combined = new ArrayList<>(postsFromDB.size() + postsFromCache.size());
            combined.addAll(postsFromDB);
            postsFromCache.values().stream()
                    .forEach(json -> combined.add(fromJson(json)));

            // Collect Result
            posts = combined;
        } else {
        	// Read DB
			List<PostDAO> fromDB = dynamodbOperation.queryPosts(friend, Map.of());

			// Write Timeline Cache
			List<String> postIds = fromDB.stream().map(post -> post.getId()).collect(Collectors.toList());
			redisOperation.setFullList(friend, postIds.size(), postIds);

			// Write Content Cache
			Map<String, String> createMap = new HashMap<>();
			for (PostDAO post : fromDB) {
				createMap.put(post.getId(), toJson(post));
			}
			redisOperation.putMultiKeys(createMap);

			posts = fromDB;
        }
    	
    	posts.sort((e1, e2) -> compLong(e1.getTimestamp(), e2.getTimestamp()));
    	
    	return posts;
    }
    
    private PostDAO fromJson(String json) {
        return new Gson().fromJson(json, PostDAO.class);
    }

    private String toJson(PostDAO post) {
        return new Gson().toJson(post);
    }
    
    private int compLong(long a1, long a2) {
    	return a1<a2?-1:(a2>a1?1:0);
    }
}
