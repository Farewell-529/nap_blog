package com.nap_blog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class NapBlogApplication {

	public static void main(String[] args) {
		SpringApplication.run(NapBlogApplication.class, args);
	}

}
