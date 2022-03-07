package com.suneo.flag.cache.module;

import com.google.inject.*;
import com.suneo.flag.cache.RedisOperation;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.util.SafeEncoder;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RedisClusterConfigModule extends AbstractModule {
    @Provides
    @Singleton
    public JedisCluster getRedisCluster() {
        String hostsandPorts = System.getenv("REDIS_HOST_PORT");
        Set<HostAndPort> hostAndPorts = new HashSet<>();
        Arrays.asList(hostsandPorts.split(",")).stream().forEach(e->{
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

    public static void main(String[] args) {
        HostAndPort hostAndPort = new HostAndPort("54.202.246.23", 6379);

        Jedis jedis = new Jedis(hostAndPort);

        List<Object> slots = jedis.clusterSlots();
        List slotInfo = null;
        for(Object obj : slots) {
            slotInfo = (List)obj;
            if(slotInfo.size()>2) {
                break;
            }
        }

        int from = ((Long)slotInfo.get(0)).intValue();
        int to = ((Long) slotInfo.get(1)).intValue();

        int size = to - from + 1;

        for(int i = 2; i < size; ++i) {
            List<Object> hostInfos = (List)slotInfo.get(i);
            if (!hostInfos.isEmpty()) {
                String host = SafeEncoder.encode((byte[])((byte[])hostInfos.get(0)));
                int port = ((Long)hostInfos.get(1)).intValue();
                System.out.println(host+":"+port);
            }
        }
    }
}
