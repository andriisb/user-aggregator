package com.user_aggregator;

import com.user_aggregator.config.AggregatorProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class})
@EnableConfigurationProperties(AggregatorProperties.class)
public class UserAggregatorApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserAggregatorApplication.class, args);
	}
}
