package com.suneo.flag.handler;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import com.google.gson.Gson;
import com.suneo.flag.db.dao.PostDAO;

@Configuration
public class PullBlogByUserHandler implements Handler{
	@Autowired
    private PostStorageManager postStorageManager;

	@Override
	public Map<String, Object> handle(Map<String, Object> params) {
		String userId = params.get(Constants.USER_NAME).toString();
		
		List<PostDAO> list = postStorageManager.queryUserTimeline(userId);
		
		String resultJson = new Gson().toJson(list);
		
		return Map.of(Constants.PULLED_POSTS, resultJson);
	}
	
    @Override
    public boolean valid(Map<String, Object> params) {
        return params.containsKey(Constants.USER_NAME);
    }
}
