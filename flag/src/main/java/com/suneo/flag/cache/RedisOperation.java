package com.suneo.flag.cache;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

public class RedisOperation {
    private final JedisCluster cluster;

    private String appendFixedLenListScript;
    private String setFullListScript;

    public RedisOperation(JedisCluster cluster) {
        this.cluster = cluster;
        try {
            this.appendFixedLenListScript = Files.readString(Path.of("fixed_len_list_append.lua"));
        } catch (IOException e) {
        	e.printStackTrace();
        }
        try {
            this.setFullListScript = Files.readString(Path.of("set_full_list.lua"));
        }  catch(IOException e) {
        	e.printStackTrace();
        }
    }

    public boolean exists(String key) {
        return cluster.exists(key);
    }

    public String get(String key) {
    	return cluster.get(key);
    }
    
    public String set(String key, String value) {
    	return cluster.set(key, value);
    }
    
    public List<String> getList(String key) {
    	return cluster.lrange(key, 0, -1);
    }
    
    public void setFullList(String key, int len, List<String> values) {
    	setFullList(key, len, values, 30);
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
    
    public void appendFixedLength(String key, String value) {
    	appendFixedLength(key, value, 30);
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
    	appendFixedLength(key, value, 30);
    }
    
    public void appendFixedLength(byte[] key, byte[] value, int expire) {
        cluster.eval(appendFixedLenListScript.getBytes(),
        		Collections.singletonList(key), List.of(value, "10".getBytes(), (expire+"").getBytes()));
    }

    public void setFullList(byte[] key, int len, List<byte[]> values) {
    	setFullList(key, len, values, 30);
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

    public static void main(String[] args) {
    	Set<HostAndPort> hostAndPorts = ImmutableSet.of(new HostAndPort("127.0.0.1", 6379));
    	JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(10);
        jedisPoolConfig.setMaxWaitMillis(100);
        jedisPoolConfig.setMaxTotal(10);
        jedisPoolConfig.setMinIdle(0);
        JedisCluster cluster = new JedisCluster(hostAndPorts, 100, jedisPoolConfig);
        
        RedisOperation op = new RedisOperation(cluster);
        byte[] key = "xiaodai".getBytes();
        List<byte[]> values = ImmutableList.of("fuck1".getBytes(),"fuck2".getBytes());
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
