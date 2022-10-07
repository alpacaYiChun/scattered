package com.suneo.blogbackend.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import com.google.gson.Gson;
import com.suneo.cachemanager.cache.RedisOperation;
import com.suneo.datamodel.db.dao.PostDAO;
import com.suneo.datamodel.db.operation.DynamodbOperation;

@Configuration
public class PostStorageManager {
	private static final Logger logger = LogManager.getLogger(PostStorageManager.class);

	@Autowired
	private DynamodbOperation dynamodbOperation;

	@Autowired
	private RedisOperation redisOperation;

	public List<PostDAO> queryUserTimeline(String friend) {
		logger.info("Querying timeline for {}", friend);

		long timelineExpire = Long.parseLong(System.getenv("TIMELINE_EXPIRE"));
		long blogExpire = Long.parseLong(System.getenv("BLOG_EXPIRE"));

		try {
			List<PostDAO> posts = null;

			if (redisOperation.exists(friend)) {
				logger.info("{} recent blog list is in cache", friend);
				// List of PostIDs
				List<String> ids = redisOperation.getList(friend);

				StringBuilder sb = new StringBuilder();
				for (String id : ids) {
					sb.append(id).append(";");
				}
				logger.info("Got Post Ids from cache: {}", sb.toString());

				posts = this.fetchPosts(ids);
			} else {
				logger.info("{} recent blog list is not in cache", friend);
				// Read DB
				List<PostDAO> fromDB = dynamodbOperation.queryPosts(friend, Map.of());

				StringBuilder sb = new StringBuilder();
				for (PostDAO post : fromDB) {
					sb.append(post.getId()).append(";");
				}
				logger.info("Found from DB {}'s posts: {}", friend, sb.toString());

				// Write Timeline Cache
				List<String> postIds = fromDB.stream().map(post -> post.getId()).collect(Collectors.toList());
				redisOperation.setFullList(friend, postIds, timelineExpire);

				// Write Content Cache
				for (PostDAO post : fromDB) {
					redisOperation.set(post.getId(), toJson(post), blogExpire);
				}

				// Collect Result
				posts = fromDB;
			}

			posts.sort((e1, e2) -> compLong(e1.getTimestamp(), e2.getTimestamp()));

			return posts;
		} catch (Exception e) {
			logger.error(ExceptionUtils.getStackTrace(e));
			return List.of();
		}
	}

	public List<PostDAO> fetchPosts(List<String> ids) {
		// List of Posts from Cache
		Map<String, String> postsFromCache = redisOperation.getMultiKeys(ids.toArray(new String[0]));

		// List of Posts from DB
		List<PostDAO> postsFromDB = ids.stream().filter(id -> !postsFromCache.containsKey(id)).map(id -> {
			try {
				logger.info("Getting post content from DB: {}", id);
				return dynamodbOperation.loadPost(id);
			} catch (Exception ex) {
				return null;
			}
		}).filter(e -> e != null).collect(Collectors.toList());

		// Write Content Cache
		Map<String, String> createMap = new HashMap<>();
		for (PostDAO post : postsFromDB) {
			createMap.put(post.getId(), toJson(post));
		}
		if (createMap.size() > 0) {
			redisOperation.putMultiKeys(createMap);
		}

		List<PostDAO> combined = new ArrayList<>(postsFromDB.size() + postsFromCache.size());
		combined.addAll(postsFromDB);
		postsFromCache.values().forEach(json -> combined.add(fromJson(json)));

		return combined;
	}

	private PostDAO fromJson(String json) {
		return new Gson().fromJson(json, PostDAO.class);
	}

	private String toJson(PostDAO post) {
		return new Gson().toJson(post);
	}

	private int compLong(long a1, long a2) {
		return Long.compare(a1, a2);
	}
}
