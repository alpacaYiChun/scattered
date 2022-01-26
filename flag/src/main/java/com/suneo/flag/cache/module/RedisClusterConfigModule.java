package com.suneo.flag.cache.module;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.suneo.flag.cache.RedisOperation;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class RedisClusterConfigModule extends AbstractModule {
    @Provides
    @Singleton
    public JedisCluster getRedisCluster() {
        Set<HostAndPort> hostAndPorts = new HashSet<>();
        Arrays.asList("".split(",")).stream().forEach(e->{
            String[] parts = e.split(":");
            String host = parts[0];
            int port = Integer.parseInt(parts[1]);
            hostAndPorts.add(new HostAndPort(host,port));
        });

        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(500);
        jedisPoolConfig.setMaxWaitMillis(2000);
        jedisPoolConfig.setMaxTotal(100);
        jedisPoolConfig.setMinIdle(0);

        return new JedisCluster(hostAndPorts, 5000, jedisPoolConfig);
    }

    @Provides
    @Singleton
    public RedisOperation getRedisOperation(JedisCluster cluster) {
        return new RedisOperation(cluster);
    }

    @Override
    public void configure() {

    }
}
