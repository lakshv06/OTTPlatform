package com.laksh.ott_enterprise;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class OttEnterpriseApplication {

	public static void main(String[] args) {
		SpringApplication.run(OttEnterpriseApplication.class, args);
	}

}
