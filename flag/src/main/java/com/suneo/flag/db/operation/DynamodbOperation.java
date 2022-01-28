package com.suneo.flag.db.operation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.suneo.flag.db.dao.CommentDAO;
import com.suneo.flag.db.dao.FollowDAO;
import com.suneo.flag.db.dao.PostDAO;
import com.suneo.flag.db.dao.UserDAO;
import com.suneo.flag.db.module.DBModule;

public class DynamodbOperation {
	private final DynamoDBMapper mapper;

	public DynamodbOperation(DynamoDBMapper mapper) {
		this.mapper = mapper;
	}
		
	public PostDAO loadPost(String id) {
		PostDAO ret = PostDAO.builder().id(id).build();
		return loadPost(ret);
	}
	
	public PostDAO loadPost(PostDAO postDAO) {
		return mapper.load(postDAO);
	}
	
	public List<PostDAO> loadPosts(List<PostDAO> daos) {
		List<PostDAO> ret = new ArrayList<>(daos.size());
		for(Object obj:mapper.batchLoad(daos).get("Post")) {
			ret.add((PostDAO)obj);
		}
		return ret;
	}
	
	public CommentDAO loadComment(String commentId) {
		CommentDAO ret = new CommentDAO();
		ret.setCommentId(commentId);
		return loadComment(ret);
	}
	
	public CommentDAO loadComment(CommentDAO commentDAO) {
		return mapper.load(commentDAO);
	}
	
	public List<CommentDAO> loadComments(List<CommentDAO> daos) {
		List<CommentDAO> ret = new ArrayList<>(daos.size());
		for(Object obj:mapper.batchLoad(daos).get("Comment")) {
			ret.add((CommentDAO)obj);
		}
		return ret;
	}
	
	public UserDAO loadUser(String userId) {
		UserDAO ret = new UserDAO();
		ret.setUserId(userId);
		return loadUser(ret);
	}
	
	public UserDAO loadUser(UserDAO userDAO) {
		return mapper.load(userDAO);
	}

	public List<PostDAO> queryPosts(String userId, Map<String, Object> params) {
		long upperTimeStamp = System.currentTimeMillis();
		long lowerTimeStamp = 0;
		int limit = 10;

		if(params.containsKey("limit")) {
			limit = (Integer)params.get("limit");
		}

		if(params.containsKey("upper")) {
			upperTimeStamp = (Long)params.get("upper");
		}

		if(params.containsKey("lower")) {
			lowerTimeStamp = (Long)params.get("lower");
		}

		if(lowerTimeStamp > upperTimeStamp) {
			upperTimeStamp ^= lowerTimeStamp;
			lowerTimeStamp ^= upperTimeStamp;
			upperTimeStamp ^= lowerTimeStamp;
		}

		limit = Math.min(limit, 100);

		Map<String, AttributeValue> valueMapping = new HashMap<String, AttributeValue>();
		valueMapping.put(":v_user_id", new AttributeValue().withS(userId));
		valueMapping.put(":v_start", new AttributeValue().withN(Long.valueOf(lowerTimeStamp).toString()));
		valueMapping.put(":v_end", new AttributeValue().withN(Long.valueOf(upperTimeStamp).toString()));

		DynamoDBQueryExpression<PostDAO> expression = new DynamoDBQueryExpression<PostDAO>()
				.withIndexName("UserTimeIndex")
				.withKeyConditionExpression("TS >= :v_start and TS <= :v_end and UserId = :v_user_id")
				.withExpressionAttributeValues(valueMapping)
				.withConsistentRead(false)
				.withLimit(limit);

		return mapper.query(PostDAO.class, expression).stream().collect(Collectors.toList());
	}

	public List<PostDAO> queryPostsByUserAndTimeInterval(String userId, long start, long end) {
        Map<String, Object> params = Map.of("upper", start, "lower", end);
        return queryPosts(userId, params);
	}
	
    public List<PostDAO> queryPostsByUserAndLowerCutoff(String userId, long timestamp) {
		Map<String, Object> params = Map.of("lower", timestamp);
		return queryPosts(userId ,params);
    }

    public List<PostDAO> queryPostsByUserAndUpperCutoff(String userId, long timestamp) {
		Map<String, Object> params = Map.of("upper", timestamp);
		return queryPosts(userId ,params);
	}
    
    public List<CommentDAO> queryCommentsByPost(String postID) {
    	Map<String, AttributeValue> valueMapping = new HashMap<>();
    	valueMapping.put(":v_post_id", new AttributeValue().withS(postID));
    	
    	DynamoDBQueryExpression<CommentDAO> expression = new DynamoDBQueryExpression<CommentDAO>()
    			.withIndexName("PostIDCommentIndex")
    			.withKeyConditionExpression("PostId = :v_post_id")
    			.withExpressionAttributeValues(valueMapping)
    			.withConsistentRead(false);
    	
    	return mapper.query(CommentDAO.class, expression).stream().collect(Collectors.toList());
    }
    
    public List<FollowDAO> queryFollowsByUser(String userID) {
    	Map<String, AttributeValue> valueMapping = new HashMap<>();
    	valueMapping.put(":v_user_id", new AttributeValue().withS(userID));
    	
    	DynamoDBQueryExpression<FollowDAO> expression = new DynamoDBQueryExpression<FollowDAO>()
    			.withKeyConditionExpression("UserId = :v_user_id")
    			.withExpressionAttributeValues(valueMapping)
    			.withConsistentRead(false);
    	return mapper.query(FollowDAO.class, expression).stream().collect(Collectors.toList());
    }
    
    public void insertPost(PostDAO post) {
    	mapper.save(post);
    }
    
    public void insertPosts(List<PostDAO> posts) {
    	mapper.batchSave(posts);
    }
        
    public void insertComment(CommentDAO comment) {
    	mapper.save(comment);
    }
    
    public void insertComments(List<CommentDAO> comments) {
    	mapper.batchSave(comments);
    }
    
    public void insertUser(UserDAO user) {
    	mapper.save(user);
    }
    
    public void insertUsers(List<UserDAO> users) {
    	mapper.batchSave(users);
    }
    
    public void insertFollow(FollowDAO follow) {
    	mapper.save(follow);
    }
    
    public void insertFollows(List<FollowDAO> follows) {
    	mapper.batchSave(follows);
    }

    // Below are tests

    public void testInsertPost() {
    	PostDAO post = PostDAO.builder()
    			.content("Suneo created some very smelly socks, you smell?")
    			.id("SuneoStink")
    			.userId("suneo-003")
    			.timestamp(2002)
    			.tags(new ArrayList<String>())
    			.build();
    	
        insertPost(post);
    }
    
    public void testQueryPost() {
    	List<PostDAO> list = queryPostsByUserAndLowerCutoff("suneo-003", 2001);
    	for(PostDAO post : list) {
    		System.out.println("TianYiMing: " + post.toString());
    	}
    }
    
    public void testInsertComment() {
    	CommentDAO comment = CommentDAO.builder()
    			.commentId("nobita-2")
    			.content("not as smelly as mine")
    			.postId("SuneoStink")
    			.userId("Nobita")
    			.parentId("NULL")
    			.ts(5001)
    			.build();    	
    	CommentDAO comment2 = CommentDAO.builder()
    			.commentId("gian-3")
    			.content("have you smelled mine?")
    			.postId("SuneoStink")
    			.userId("Gian")
    			.parentId("NULL")
    			.ts(5007)
    			.build();
    	insertComment(comment);
    	insertComment(comment2);
    }
    
    public void testQueryComment() {
        List<CommentDAO> comments = queryCommentsByPost("SuneoStink");
        List<CommentDAO> loadedComments = loadComments(comments);
        
        for(CommentDAO dao:loadedComments) {
        	System.out.println(dao.toString());
        }
    }
    
    public void testInsertUser() {
    	UserDAO userDAO = UserDAO.builder()
    			.userId("Nobita")
    			.name("Nobita Yebi")
    			.description("Very Lazy")
    			.build();
    	insertUser(userDAO);
    }
    
    public void testQueryUser() {
    	UserDAO dao = this.loadUser("Nobita");
    	System.out.println(dao.toString());
    }
    
    public void testInsertFollow() {
    	FollowDAO f1 = FollowDAO.builder()
    			.userId("Suneo")
    			.following("TianYiMing")
    			.build();
    	
    	FollowDAO f2 = FollowDAO.builder()
    			.userId("Nobita")
    			.following("FangKe")
    			.build();    	
    	insertFollows(List.of(f1,f2));
    }
    
    public void testQueryFollow() {
    	queryFollowsByUser("Nobita").stream().forEach(e->System.out.println(e.toString()));
    	queryFollowsByUser("Suneo").stream().forEach(e->System.out.println(e.toString()));
    }
    
	public static void main(String[] args) {
		Injector injector = Guice.createInjector(new DBModule());
        DynamodbOperation operation = injector.getInstance(DynamodbOperation.class);
        
        //operation.testInsertPost();
        //operation.testQueryPost();
        
        //operation.testInsertUser();
        operation.testQueryUser();
        
        //operation.testInsertFollow();
        //operation.testQueryFollow();
        
        //operation.testInsertComment();
        //operation.testQueryComment();
	}
}
