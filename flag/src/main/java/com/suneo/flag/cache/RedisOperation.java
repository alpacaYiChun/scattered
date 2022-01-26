package com.suneo.flag.cache;

import redis.clients.jedis.JedisCluster;
import java.util.List;

public class RedisOperation {
    private final JedisCluster cluster;

    public RedisOperation(JedisCluster cluster) {
        this.cluster = cluster;
    }

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

    public void append(byte[] key, byte[] value) {
        cluster.rpush(key, value);
    }
}
