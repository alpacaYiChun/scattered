package com.suneo.flag.cache.bean;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.suneo.flag.cache.RedisOperation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
public class RedisClusterSpringConfiguration {
	@Value("${spring.redis.cluster.nodes}")
	private String nodes;
	
	@Value("${spring.redis.pool.max-active:100}")
	private int maxActive;
	
	@Value("${spring.redis.pool.max-idle:500}")
	private int maxIdle;
	
	@Value("${spring.redis.pool.min-idle:0}")
	private int minIdle;
	
	@Value("${spring.redis.pool.max-wait:2000}")
	private int maxConnectWait;
	
	@Value("${spring.redis.cluster.command-timeout:5000}")
	private int commandTimeout;
	
	@Bean
	public JedisCluster getRedisCluster() {
		Set<HostAndPort> hostAndPorts = new HashSet<>();
		Arrays.asList(nodes.split(",")).stream().forEach(e->{
			String[] parts = e.split(":");
			String host = parts[0];
			int port = Integer.parseInt(parts[1]);
			hostAndPorts.add(new HostAndPort(host,port));
		});
		
		JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(maxIdle);
        jedisPoolConfig.setMaxWaitMillis(maxConnectWait);
        jedisPoolConfig.setMaxTotal(maxActive);
        jedisPoolConfig.setMinIdle(minIdle);
		
		return new JedisCluster(hostAndPorts, commandTimeout, jedisPoolConfig);
	}

	@Bean
	public RedisOperation getRedisOperation(JedisCluster cluster) {
		return new RedisOperation(cluster);
	}
}
