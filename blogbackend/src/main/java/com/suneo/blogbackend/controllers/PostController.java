package com.suneo.blogbackend.controllers;

import org.springframework.web.bind.annotation.RestController;

import com.suneo.blogbackend.Constants;
import com.suneo.blogbackend.handler.PullBlogByUserHandler;
import com.suneo.blogbackend.handler.PullBlogsHandler;
import com.suneo.shared.clients.KinesisMessageQueue;

import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
public class PostController {
	@Autowired
	private KinesisMessageQueue kinesisQueue;
	
	@Autowired
	private PullBlogsHandler pullBlogsHandler;
	
	@Autowired
	private PullBlogByUserHandler pullBlogByUserHandler;
	
    @PostMapping("create")
    public int createPost(@RequestBody String json) {
    	long timestamp = System.currentTimeMillis();
    	try {
    		JSONObject jobj = new JSONObject(json);
    		String content = jobj.getString("content");
    		String userId = jobj.getString("user");
    		if(content.length() > 140) {
    			content = content.substring(0, 140);
    		}
    		
    		JSONObject eventJson = new JSONObject();
    		eventJson.put("UserId", userId);
    		eventJson.put("Content", content);
    		eventJson.put("Timestamp", timestamp);
    		
    		kinesisQueue.sendMessage("SuneoStream", userId, eventJson.toString());
    		
    		return 200;
    	} catch (Exception e) {
    		return 500;
    	}
    }
    
    @GetMapping("pull/{username}")
    public String pullPosts(@PathVariable String username) {
    	Map<String, Object> params = Map.of(Constants.USER_NAME, username);
    	try {
	    	Map<String, Object> result = pullBlogsHandler.handle(params);
	    	return result.get(Constants.PULLED_POSTS).toString();
    	} catch (Exception e) {
    		return e.toString();
    	}
    }
    
    @GetMapping("pullbyuser/{username}")
    public String pullPostsByUser(@PathVariable String username) {
    	Map<String, Object> params = Map.of(Constants.USER_NAME, username);
    	try {
    		Map<String, Object> result = pullBlogByUserHandler.handle(params);
    		return result.get(Constants.PULLED_POSTS).toString();
    	} catch (Exception e) {
    		return e.toString();
    	}
    }
}
