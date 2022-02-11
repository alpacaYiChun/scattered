package com.suneo.flag.lambda;

import java.util.ArrayList;
import java.util.List;

import com.suneo.flag.cache.CacheStrategy;
import com.suneo.flag.cache.RedisOperation;
import com.suneo.flag.cache.module.RedisClusterConfigModule;
import org.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	private static final Logger logger = LoggerFactory.getLogger(KinesisBlogProcessor.class);
	
	private DynamodbOperation db;
	private RedisOperation redisOperation;
	private final CacheStrategy<PostDAO> strategy;
	
	public KinesisBlogProcessor() {
		logger.info("Begin initialize Alpaca Lambda");
	    Injector injector = Guice.createInjector(new DBModule(), new RedisClusterConfigModule());
	    logger.info("Begin initialize Alpaca DB Mapper");
	    try {
	    	this.db = injector.getInstance(DynamodbOperation.class);
	    } catch (Exception e) {
	    	this.db = null;
	    	logger.error(String.format("Alpaca Unable to initialize DB: %s", e.getMessage()));
	    }
	    logger.info("Begin initialize Alpaca Redis Mapper");
	    try {
	    	this.redisOperation = injector.getInstance(RedisOperation.class);
	    } catch (Exception e) {
	    	this.redisOperation = null;
	    	logger.error(String.format("Alpaca Unable to initialize Cache: %s", e.getMessage()));
	    }
	    this.strategy = new AlwaysCacheStrategy();
	}
	
	@Override
	public String handleRequest(KinesisEvent input, Context context) {
		if(this.db == null ||this.redisOperation == null) {
			return "Unable to Process Request since the data storage is badly wired up!";
		}
		
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
	    } catch (Exception e) {
	    	logger.error(String.format("Alpaca Unable to Add Posts to DB: %s", e.getMessage()));
	    	insertToDbOK = false;
	    }
	    
	    boolean insertToCacheOK = true;
	    try {
	    	for(PostDAO postDAO : list) {
	    		if (!strategy.shouldCache(postDAO)) {
	    			continue;
				}
	    		redisOperation.appendFixedLength(postDAO.getUserId(), postDAO.getId());
			}
	    } catch (Exception e) {
	    	logger.error(String.format("Alpaca Unable to Add Posts ot Cache %s", e.getMessage()));
	    	insertToCacheOK = false;
	    }
	    
	    String finalResult = String.format("GoodJson %d, badJson %d, inserted DB: %b, insertedCache: %b", goodJson, badJson, insertToDbOK, insertToCacheOK);
	    
	    logger.info("Alpaca Finally: %s", finalResult);
	    
		return finalResult;
	}

	private static class AlwaysCacheStrategy implements CacheStrategy<PostDAO> {
		@Override
		public boolean shouldCache(PostDAO item) {
			return true;
		}
	}
}
