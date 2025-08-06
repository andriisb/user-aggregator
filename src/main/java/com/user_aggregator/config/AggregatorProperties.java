package com.user_aggregator.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "aggregator")
@Data
public class AggregatorProperties {
    private List<DataSourceProperties> dataSources;
}
