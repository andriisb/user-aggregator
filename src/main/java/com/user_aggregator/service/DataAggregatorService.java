package com.user_aggregator.service;

import com.user_aggregator.config.AggregatorProperties;
import com.user_aggregator.config.DataSourceProperties;
import com.user_aggregator.entity.UserEntity;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DataAggregatorService {

    private final Map<String, DataSource> dataSources;
    private final AggregatorProperties aggregatorProperties;

    private final ExecutorService executor = Executors.newFixedThreadPool(5);

    public List<UserEntity> fetchAllUsers() {
        List<CompletableFuture<List<UserEntity>>> futures = createFuturesForAllDataSources();
        return aggregateFutures(futures);
    }

    private List<CompletableFuture<List<UserEntity>>> createFuturesForAllDataSources() {
        return aggregatorProperties.getDataSources().stream()
                .map(source -> CompletableFuture.supplyAsync(() -> fetchUsersWithFallback(source), executor))
                .toList();
    }

    private List<UserEntity> aggregateFutures(List<CompletableFuture<List<UserEntity>>> futures) {
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

        return allFutures.thenApply(v -> futures.stream()
                        .map(CompletableFuture::join)
                        .flatMap(List::stream)
                        .distinct()
                        .collect(Collectors.toList()))
                .join();
    }

    private List<UserEntity> fetchUsersWithFallback(DataSourceProperties source) {
        try {
            return fetchUsersFromDataSource(source);
        } catch (Exception e) {
            log.error("Error fetching users from datasource [{}]: {}", source.getName(), e.getMessage());
            return List.of();
        }
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
                .filter(key -> !mapping.containsKey(key)) // Check for missing keys
                .findFirst()
                .ifPresent(key -> {
                    throw new IllegalArgumentException(
                            String.format("Missing required mapping key '%s' in data source: %s", key, source.getName())
                    );
                });
    }

    @PreDestroy
    public void shutdownExecutor() {
        executor.shutdown();
        log.info("ExecutorService has been shut down.");
    }
}
