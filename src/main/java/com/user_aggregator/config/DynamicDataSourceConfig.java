package com.user_aggregator.config;

import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
@RequiredArgsConstructor
public class DynamicDataSourceConfig {

    private final AggregatorProperties aggregatorProperties;

    @Bean
    public Map<String, DataSource> dataSources() {
        return aggregatorProperties.getDataSources().stream()
                .collect(Collectors.toMap(DataSourceProperties::getName, this::createDataSource));
    }

    private HikariDataSource createDataSource(DataSourceProperties source) {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(source.getUrl());
        dataSource.setUsername(source.getUser());
        dataSource.setPassword(source.getPassword());
        return dataSource;
    }
}
