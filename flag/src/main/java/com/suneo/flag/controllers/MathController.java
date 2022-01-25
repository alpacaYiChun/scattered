package com.suneo.flag.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.suneo.flag.cache.bean.RedisOperation;

@RestController
public class MathController {
	@Autowired
	private RedisOperation jedis;
	
	@GetMapping("getkey/{key}")
	public String getkey(@PathVariable String key) {
		return jedis.get(key);
	}
    @GetMapping("add/{a}/{b}")
    public Integer add(@PathVariable int a, @PathVariable int b) {
    	return a + b;
    }
}
