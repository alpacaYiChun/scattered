package com.suneo.blogbackend;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.suneo.blogbackend.handler.PostStorageManager;

@SpringBootApplication
public class BlogApplication {
	private static final Logger logger = LogManager.getLogger(PostStorageManager.class);
	
	public static void main(String[] args) {
		logger.info("Starting up BlogApplication for Alpaca");
		
		SpringApplication.run(BlogApplication.class, args);
	}
}
