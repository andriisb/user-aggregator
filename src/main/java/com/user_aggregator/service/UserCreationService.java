package com.user_aggregator.service;

import com.user_aggregator.config.AggregatorProperties;
import com.user_aggregator.config.DataSourceProperties;
import com.user_aggregator.dto.UserCreationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserCreationService {

    private final Map<String, DataSource> dataSources;
    private final AggregatorProperties aggregatorProperties;

    public String createUser(UserCreationRequest request) {
        log.info("Attempting to create user {} in database {}", request.getUsername(), request.getDatabaseName());

        DataSourceProperties sourceProperties = aggregatorProperties.getDataSources().stream()
                .filter(source -> request.getDatabaseName() != null && source.getName().equals(request.getDatabaseName()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Database not found: " + request.getDatabaseName()));

        DataSource dataSource = dataSources.get(sourceProperties.getName());
        if (dataSource == null) {
            throw new IllegalArgumentException("DataSource bean not found for: " + request.getDatabaseName());
        }

        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        String insertQuery = buildInsertQuery(sourceProperties);

        List<Object> params = new ArrayList<>();
        Map<String, String> mapping = sourceProperties.getMapping();
        String idColumn = mapping.get("id");
        String usernameColumn = mapping.get("username");

        Object idValue;
        if (idColumn.equals(usernameColumn)) {
            idValue = request.getUsername();
            params.add(idValue);
        } else {
            idValue = "user_id".equals(idColumn) ? UUID.randomUUID() : request.getUsername();
            params.add(idValue);
            params.add(request.getUsername());
        }
        params.add(request.getName());
        params.add(request.getSurname());
        jdbcTemplate.update(insertQuery, params.toArray());

        log.info("Successfully created user {} in database {}", request.getUsername(), request.getDatabaseName());
        return String.format("User %s successfully created in database %s.", request.getUsername(), request.getDatabaseName());
    }

    private String buildInsertQuery(DataSourceProperties source) {
        Map<String, String> mapping = source.getMapping();
        String table = source.getTable();
        List<String> columns = new ArrayList<>();
        List<String> placeholders = new ArrayList<>();
        columns.add(mapping.get("id"));
        placeholders.add("?");

        if (!mapping.get("id").equals(mapping.get("username"))) {
            columns.add(mapping.get("username"));
            placeholders.add("?");
        }

        columns.add(mapping.get("name"));
        placeholders.add("?");
        columns.add(mapping.get("surname"));
        placeholders.add("?");

        return String.format(
                "INSERT INTO %s (%s) VALUES (%s)",
                table,
                String.join(", ", columns),
                String.join(", ", placeholders)
        );
    }
}
