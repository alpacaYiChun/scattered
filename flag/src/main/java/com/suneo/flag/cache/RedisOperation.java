package com.suneo.flag.cache;

import redis.clients.jedis.JedisCluster;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RedisOperation {
    private final JedisCluster cluster;

    private String appendFixedLenListScript;
    private String setFullListScript;

    public RedisOperation(JedisCluster cluster) {
        this.cluster = cluster;
        try {
            this.appendFixedLenListScript = Files.readString(Path.of("scripts/fixed_len_list_append.lua"));
        } catch (IOException e) {
            this.appendFixedLenListScript = "local num = redis.call('llen', KEYS[1]);\n" +
                    "local limit = tonumber(ARGV[2])\n" +
                    "if num >= limit then\n" +
                    "\tredis.call('lpop', KEYS[1])\n" +
                    "end\n" +
                    "redis.call('rpush', KEYS[1], ARGV[1])\n" +
                    "return redis.call('llen', KEYS[1])";
        }
        try {
            this.setFullListScript = Files.readString(Path.of("scripts/set_full_list.lua"));
        }  catch(IOException e) {
            this.setFullListScript = "local len = tonumber(ARGV[1])\n" +
                    "for i = 2, len + 1\n" +
                    "do\n" +
                    "\tredis.call('rpush', KEYS[1], ARGV[i])\n" +
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
    	return cluster.set(key, value);
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
        cluster.eval(appendFixedLenListScript.getBytes(), Collections.singletonList(key), List.of(value, "10".getBytes()));
    }

    public void setFullList(byte[] key, int len, List<byte[]> values) {
        List<byte[]> toUse = new ArrayList<>(values.size() + 1);
        toUse.add((values.size()+"").getBytes());
        for(byte[] value: values) {
            toUse.add(value);
        }
        cluster.eval(setFullListScript.getBytes(), Collections.singletonList(key), toUse);
    }

    public static void main() {

    }
}
