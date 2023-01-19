package com.flab.oasis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class OasisApplication {

	public static void main(String[] args) {
		SpringApplication.run(OasisApplication.class, args);
	}

}
