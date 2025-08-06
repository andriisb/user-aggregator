package com.user_aggregator.service;

import com.user_aggregator.config.AggregatorProperties;
import com.user_aggregator.config.DataSourceProperties;
import com.user_aggregator.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DataAggregatorService {

    private final Map<String, DataSource> dataSources;
    private final AggregatorProperties aggregatorProperties;

    public Flux<UserEntity> fetchAllUsers() {
        return Flux.fromIterable(aggregatorProperties.getDataSources())
                .flatMap(this::fetchUsersWithFallback)
                .flatMap(Flux::fromIterable)
                .distinct();
    }

    private Mono<List<UserEntity>> fetchUsersWithFallback(DataSourceProperties source) {
        return Mono.fromCallable(() -> fetchUsersFromDataSource(source))
                .onErrorReturn(Collections.emptyList());
    }

    private List<UserEntity> fetchUsersFromDataSource(DataSourceProperties source) {
        log.info("Fetching users from datasource [{}]", source.getName());
        validateMappings(source);
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSources.get(source.getName()));
        String query = buildQuery(source);
        return jdbcTemplate.query(query, (rs, rowNum) ->
                new UserEntity(
                        rs.getString("id"),
                        rs.getString("username"),
                        rs.getString("name"),
                        rs.getString("surname")
                ));
    }

    private String buildQuery(DataSourceProperties source) {
        return String.format("SELECT %s as id, %s as username, %s as name, %s as surname FROM %s",
                source.getMapping().get("id"),
                source.getMapping().get("username"),
                source.getMapping().get("name"),
                source.getMapping().get("surname"),
                source.getTable()
        );
    }

    private void validateMappings(DataSourceProperties source) {
        List<String> requiredKeys = List.of("id", "username", "name", "surname");

        Map<String, String> mapping = Optional.ofNullable(source.getMapping())
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("Mappings are missing in data source: %s", source.getName())));

        requiredKeys.stream()
                .filter(key -> !mapping.containsKey(key))
                .findFirst()
                .ifPresent(key -> {
                    throw new IllegalArgumentException(
                            String.format("Missing required mapping key '%s' in data source: %s", key, source.getName())
                    );
                });
    }
}

