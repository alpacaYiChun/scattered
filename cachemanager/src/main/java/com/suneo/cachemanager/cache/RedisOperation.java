package com.suneo.cachemanager.cache;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class RedisOperation {
    private final JedisCluster cluster;

    private String appendFixedLenListScript;
    private String setFullListScript;
    private String incrKeysScript;

    public RedisOperation(JedisCluster cluster) {
        this.cluster = cluster;
        try {
            this.appendFixedLenListScript = Files.readString(Path.of("fixed_len_list_append.lua"));
        } catch (IOException e) {
        	this.appendFixedLenListScript = "local num = redis.call('llen', KEYS[1]);\n" + 
        			"local limit = tonumber(ARGV[2])\n" + 
        			"local expire = tonumber(ARGV[3])\n" + 
        			"if num >= limit then\n" + 
        			"	redis.call('lpop', KEYS[1])\n" + 
        			"end\n" + 
        			"redis.call('rpush', KEYS[1], ARGV[1])\n" + 
        			"redis.call('expire', KEYS[1], expire)\n" + 
        			"return redis.call('llen', KEYS[1])";
        }
        try {
            this.setFullListScript = Files.readString(Path.of("set_full_list.lua"));
        }  catch(IOException e) {
        	this.setFullListScript = "local already = redis.call('exists', KEYS[1])\n" + 
        			"if already == 0 then\n" + 
        			"	local len = tonumber(ARGV[1])\n" + 
        			"	local expire = tonumber(ARGV[2])\n" + 
        			"	for i = 3, len + 2\n" + 
        			"	do\n" + 
        			"		redis.call('rpush', KEYS[1], ARGV[i])\n" + 
        			"	end\n" + 
        			"	redis.call('expire', KEYS[1], expire)\n" + 
        			"end";
        }
        try {
            this.incrKeysScript = Files.readString(Path.of("incr_keys_by.lua"));
        } catch (IOException e) {
            this.incrKeysScript = "local len = tonumber(ARGV[1])\n" + 
            		"local expire = tonumber(ARGV[2])\n" + 
            		"\n" + 
            		"for i=1, len\n" + 
            		"do\n" + 
            		"    redis.call('incrby', KEYS[i], ARGV[i+2])\n" + 
            		"    redis.call('expire', KEYS[i], expire)\n" + 
            		"end";
        }
    }

    public boolean exists(String key) {
        return cluster.exists(key);
    }

    public String get(String key) {
    	return cluster.get(key);
    }
    
    public String set(String key, String value) {
    	return cluster.setex(key, 2 * 60 * 60, value);
    }
    
    public List<String> getList(String key) {
    	return cluster.lrange(key, 0, -1);
    }
    
    public void setFullList(String key, int len, List<String> values) {
    	setFullList(key, len, values, 2*60*60);
    }
    
    public void setFullList(String key, int len, List<String> values, int expire) {
        List<String> toUse = new ArrayList<>(values.size() + 2);
        toUse.add((values.size()+""));
        toUse.add((expire+""));
        for(String value: values) {
            toUse.add(value);
        }
        cluster.eval(setFullListScript, Collections.singletonList(key), toUse);
    }

    public void incKeysBy(List<String> keys, List<Integer> values, int expire) {
        List<String> params = new ArrayList<>();
        params.add(values.size()+"");
        params.add(expire+"");
        for(Integer val : values) {
            params.add(val+"");
        }
        cluster.eval(incrKeysScript, keys, params);
    }
    
    public void appendFixedLength(String key, String value) {
    	appendFixedLength(key, value, 2 * 60 * 60);
    }
    
    public void appendFixedLength(String key, String value, int expire) {
    	cluster.eval(appendFixedLenListScript,
        		Collections.singletonList(key), List.of(value, "10", expire+""));
    }
    
    public byte[] get(byte[] key) {
        return cluster.get(key);
    }

    public String set(byte[] key, byte[] value) {
        return cluster.set(key, value);
    }

    public List<byte[]> getList(byte[] key) {
        return cluster.lrange(key, 0, -1);
    }

    public void appendFixedLength(byte[] key, byte[] value) {
    	appendFixedLength(key, value, 2 * 60 * 60);
    }
    
    public void appendFixedLength(byte[] key, byte[] value, int expire) {
        cluster.eval(appendFixedLenListScript.getBytes(),
        		Collections.singletonList(key), List.of(value, "10".getBytes(), (expire+"").getBytes()));
    }

    public void setFullList(byte[] key, int len, List<byte[]> values) {
    	setFullList(key, len, values, 2 * 60 * 60);
    }
    
    public void setFullList(byte[] key, int len, List<byte[]> values, int expire) {
        List<byte[]> toUse = new ArrayList<>(values.size() + 2);
        toUse.add((values.size()+"").getBytes());
        toUse.add((expire+"").getBytes());
        for(byte[] value: values) {
            toUse.add(value);
        }
        cluster.eval(setFullListScript.getBytes(), Collections.singletonList(key), toUse);
    }

    public Map<String, String> getMultiKeys(String[] keys) {
        List<String> attempt = cluster.mget(keys);
        Map<String, String> ret = new HashMap<>();
        for(int i = 0; i < attempt.size(); i++) {
            if(attempt.get(i) != null) {
                ret.put(keys[i], attempt.get(i));
            }
        }
        return ret;
    }

    public void putMultiKeys(Map<String, String> kvs) {
        String[] kvsArray = new String[kvs.size() * 2];
        int i = 0;
        for(Map.Entry<String, String> e : kvs.entrySet()) {
            kvsArray[i++] = e.getKey();
            kvsArray[i++] = e.getValue();
        }
        cluster.mset(kvsArray);
    }

    public static void main(String[] args) {
    	Set<HostAndPort> hostAndPorts = Set.of(new HostAndPort("127.0.0.1", 6379));
    	JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(10);
        jedisPoolConfig.setMaxWaitMillis(100);
        jedisPoolConfig.setMaxTotal(10);
        jedisPoolConfig.setMinIdle(0);
        JedisCluster cluster = new JedisCluster(hostAndPorts, 100, jedisPoolConfig);
        
        RedisOperation op = new RedisOperation(cluster);
        byte[] key = "xiaodai".getBytes();
        List<byte[]> values = List.of("fuck1".getBytes(),"fuck2".getBytes());
        op.setFullList(key, 2, values, 30);
        
        for(byte[] value : op.getList(key)) {
        	System.out.println(new String(value));
        }
        
        for(int i=3;i<=11;i++) {
        	op.appendFixedLength(key, ("meimei"+i).getBytes(), 30);
        }
        
        for(byte[] value : op.getList(key)) {
        	System.out.println(new String(value));
        }
    }
}
