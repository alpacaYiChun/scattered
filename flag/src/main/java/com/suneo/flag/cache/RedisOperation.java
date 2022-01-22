package com.suneo.flag.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import redis.clients.jedis.JedisCluster;

@Service
public class RedisOperation {
    @Autowired
    private JedisCluster cluster;
    
    public String get(String key) {
    	return cluster.get(key);
    }
    
    public void set(String key, String value) {
    	cluster.set(key, value);
    }
}
