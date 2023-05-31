package com.practice.crypto;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;

@EnableCaching
@SpringBootApplication
@ComponentScan(basePackages = "com.practice.crypto")
public class CryptoHandlerApplication {

	public static void main(String[] args) {
		SpringApplication.run(CryptoHandlerApplication.class, args);
	}
}