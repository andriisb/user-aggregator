package com.user_aggregator.config;

import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class DynamicDataSourceConfig {

    private final AggregatorProperties aggregatorProperties;

    @Bean
    public Map<String, DataSource> dataSources() {
        Map<String, DataSource> dataSourceMap = new HashMap<>();
        List<DataSourceProperties> sources = aggregatorProperties.getDataSources();

        if (sources != null && !sources.isEmpty()) {
            for (DataSourceProperties source : sources) {
                HikariDataSource dataSource = new HikariDataSource();
                dataSource.setJdbcUrl(source.getUrl());
                dataSource.setUsername(source.getUser());
                dataSource.setPassword(source.getPassword());
                dataSourceMap.put(source.getName(), dataSource);
            }
        } else {
            System.out.println("No data sources configured. The 'data-sources' list is empty or null.");
        }
        return dataSourceMap;
    }
}
