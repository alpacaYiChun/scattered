package com.suneo.flag.lambda;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import com.suneo.flag.cache.CacheConstants;
import com.suneo.flag.cache.CacheStrategy;
import com.suneo.flag.cache.RedisOperation;
import com.suneo.flag.cache.module.RedisClusterConfigModule;
import com.suneo.flag.lib.SerializationUtil;
import org.json.JSONObject;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.KinesisEvent;
import com.amazonaws.services.lambda.runtime.events.KinesisEvent.KinesisEventRecord;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.suneo.flag.db.operation.DynamodbOperation;
import com.suneo.flag.db.dao.PostDAO;
import com.suneo.flag.db.module.DBModule;

public class KinesisBlogProcessor implements RequestHandler<KinesisEvent, String>{
	private final DynamodbOperation db;
	private final RedisOperation redisOperation;
	private final CacheStrategy<PostDAO> strategy;
	
	public KinesisBlogProcessor() {
	    Injector injector = Guice.createInjector(new DBModule(), new RedisClusterConfigModule());
	    this.db = injector.getInstance(DynamodbOperation.class);
	    this.redisOperation = injector.getInstance(RedisOperation.class);
	    this.strategy = new NewBlogCacheStrategy();
	}
	
	@Override
	public String handleRequest(KinesisEvent input, Context context) {
		long now = System.currentTimeMillis();
		
		int goodJson = 0;
		int badJson = 0;
		
		List<PostDAO> list = new ArrayList<>();
	    for(KinesisEventRecord record : input.getRecords()) {
	    	try {
		        String json = new String(record.getKinesis().getData().array());
		        
		        JSONObject obj = new JSONObject(json);
		        String userName = obj.getString("UserId");
		        String content = obj.getString("Content");
		        String id = String.format("%s_%d", userName, now);
		        PostDAO created = PostDAO.builder().id(id)
		            .content(content)
		            .userId(userName)
		            .timestamp(now)
		            .build();
		        
		        list.add(created);		        
		        ++goodJson;
	    	} catch (Exception e) {
	    		++badJson;
	    	}
	    	
	    }
	    
	    boolean insertToDbOK = true;
	    try {
	    	db.insertPosts(list);
	    	for(PostDAO postDAO : list) {
	    		if (!strategy.shouldCache(postDAO)) {
	    			continue;
				}
	    		long slot = postDAO.getTimestamp() / CacheConstants.INTERVAL_MILLSECONDS;
	    		String key = postDAO.getUserId() + "_" + slot;
	    		byte[] keyBytes = key.getBytes();
	    		redisOperation.append(keyBytes, SerializationUtil.serialize(postDAO));
			}
	    } catch (Exception e) {
	    	insertToDbOK = false;
	    }
	    
		return String.format("GoodJson %d, badJson %d, inserted: %b", goodJson, badJson, insertToDbOK);
	}

	private static class NewBlogCacheStrategy implements CacheStrategy<PostDAO> {
		private static long FOUR_HOUR = 4 * 60 * 60 * 1000;

		@Override
		public boolean shouldCache(PostDAO item) {
			long now = System.currentTimeMillis();
			long blogTime = item.getTimestamp();
			long gap = now - blogTime;
			return gap <= FOUR_HOUR;
		}
	}
}
