package com.utpmarket.utp_market;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
public class UtpMarketApplication {

	public static void main(String[] args) {
		SpringApplication.run(UtpMarketApplication.class, args);
	}

}