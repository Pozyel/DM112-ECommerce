package com.inatel.ecommerce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@SpringBootApplication
@ComponentScan(excludeFilters = {
        @ComponentScan.Filter(type = FilterType.REGEX, pattern = "br.inatel.client.*") })
public class ECommerceDm112Application {

	public static void main(String[] args) {
		SpringApplication.run(ECommerceDm112Application.class, args);
	}

}
