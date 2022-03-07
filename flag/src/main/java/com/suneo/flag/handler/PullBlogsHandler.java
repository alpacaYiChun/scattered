package com.suneo.flag.handler;

import com.suneo.flag.cache.RedisOperation;
import com.suneo.flag.db.dao.FollowDAO;
import com.suneo.flag.db.dao.PostDAO;
import com.suneo.flag.db.operation.DynamodbOperation;
import com.suneo.flag.lib.MergeIterator;
import com.suneo.flag.lib.SerializationUtil;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
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

                    combined.sort((e1, e2) -> compLong(e1.getTimestamp(), e2.getTimestamp()));

                    // Collect Result
                    posts.add(combined);
                } else {
                	// Read DB
                    List<PostDAO> fromDB = dynamodbOperation.queryPosts(friend, Map.of()).stream()
                            .sorted((e1, e2) -> compLong(e1.getTimestamp(), e2.getTimestamp()))
                            .collect(Collectors.toList());
                    
                    // Write Timeline Cache
                    List<String> postIds = fromDB.stream()
                    		.map(post -> post.getId())
                    		.collect(Collectors.toList());
                    redisOperation.setFullList(friend, postIds.size(), postIds);

                    // Write Content Cache
                    Map<String, String> createMap = new HashMap<>();
                    for(PostDAO post : fromDB) {
                        createMap.put(post.getId(), toJson(post));
                    }
                    redisOperation.putMultiKeys(createMap);

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

    private PostDAO fromJson(String json) {
        JSONObject obj = new JSONObject(json);
        return PostDAO.builder()
                .id(obj.getString("id"))
                .content(obj.getString("content"))
                .userId(obj.getString("userId"))
                .timestamp(obj.getLong("timestamp"))
                .build();
    }

    private String toJson(PostDAO post) {
        JSONObject obj = new JSONObject();
        obj.put("id", post.getId());
        obj.put("content", post.getContent());
        obj.put("userId", post.getUserId());
        obj.put("timestamp", post.getTimestamp());
        return obj.toString();
    }
}
