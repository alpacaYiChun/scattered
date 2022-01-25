package com.suneo.flag.cache.bean;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import redis.clients.jedis.JedisCluster;

import java.util.List;

@Service
public class RedisOperation {
    @Autowired
    private JedisCluster cluster;

    public boolean exists(String key) {
        return cluster.exists(key);
    }

    public String get(String key) {
    	return cluster.get(key);
    }
    
    public void set(String key, String value) {
    	cluster.set(key, value);
    }

    public byte[] getBinary(byte[] key) {
        return cluster.get(key);
    }
    
    public List<byte[]> getBinaryList(byte[] key){
    	return cluster.lrange(key, 0, -1);
    }
}
