package com.suneo.flag.controllers;

import org.springframework.web.bind.annotation.RestController;

import com.suneo.flag.queue.KinesisMessageQueue;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
public class PostController {
	@Autowired
	private KinesisMessageQueue kinesisQueue;
	
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
}
