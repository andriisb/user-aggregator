package com.user_aggregator.service;

import com.user_aggregator.config.AggregatorProperties;
import com.user_aggregator.config.DataSourceProperties;
import com.user_aggregator.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DataAggregatorService {

    private final Map<String, DataSource> dataSources;
    private final AggregatorProperties aggregatorProperties;

    public List<UserEntity> fetchAllUsers() {
        List<UserEntity> aggregatedUsers = new ArrayList<>();
        List<DataSourceProperties> sources = aggregatorProperties.getDataSources();

        if (sources == null) {
            return aggregatedUsers;
        }

        for (DataSourceProperties source : sources) {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSources.get(source.getName()));
            String query = String.format("SELECT %s as id, %s as username, %s as name, %s as surname FROM %s",
                    source.getMapping().get("id"),
                    source.getMapping().get("username"),
                    source.getMapping().get("name"),
                    source.getMapping().get("surname"),
                    source.getTable()
            );

            List<UserEntity> users = jdbcTemplate.query(query, (rs, rowNum) ->
                    new UserEntity(
                            rs.getString("id"),
                            rs.getString("username"),
                            rs.getString("name"),
                            rs.getString("surname")
                    )
            );

            aggregatedUsers.addAll(users);
        }

        return aggregatedUsers.stream()
                .distinct()
                .collect(Collectors.toList());
    }
}
