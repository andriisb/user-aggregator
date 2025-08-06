package com.user_aggregator.config;

import lombok.RequiredArgsConstructor;
import org.flywaydb.core.Flyway;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import javax.sql.DataSource;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class FlywayMigrationConfig {

    private final Map<String, DataSource> dataSources;

    @PostConstruct
    public void migrate() {
        if (dataSources != null && !dataSources.isEmpty()) {
            dataSources.forEach((name, dataSource) -> {
                String location = "db/migration/" + name;
                System.out.printf("Running Flyway migrations for data source: %s at location: %s%n", name, location);
                Flyway flyway = Flyway.configure()
                        .dataSource(dataSource)
                        .locations("classpath:" + location)
                        .load();

                int migratedCount = flyway.migrate().migrationsExecuted;
                System.out.printf("Successfully applied %d migrations for data source: %s%n", migratedCount, name);
            });
        }
    }
}
