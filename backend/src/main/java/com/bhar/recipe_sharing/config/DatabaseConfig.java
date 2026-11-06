package com.bhar.recipe_sharing.config;

import java.net.URI;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.zaxxer.hikari.HikariDataSource;

@Configuration
public class DatabaseConfig {

    @Bean
    @Primary
    public DataSource dataSource(
        @Value("${spring.datasource.url:jdbc:postgresql://localhost:5432/recipe_sharing}") String rawUrl,
        @Value("${spring.datasource.username:postgres}") String username,
        @Value("${spring.datasource.password:PostgreSql-226}") String password
    ) {
        if (rawUrl != null) {
            String trimmed = rawUrl.trim();

            // Render, Railway and Heroku provide database URLs starting with postgres:// or postgresql://
            // JDBC requires the URL to start with "jdbc:postgresql://"
            if (trimmed.startsWith("postgres://") || trimmed.startsWith("postgresql://")) {
                try {
                    String cleanUri = trimmed.startsWith("postgres://")
                        ? "http://" + trimmed.substring("postgres://".length())
                        : "http://" + trimmed.substring("postgresql://".length());

                    URI uri = new URI(cleanUri);
                    String userInfo = uri.getUserInfo();
                    if (userInfo != null && userInfo.contains(":")) {
                        String[] parts = userInfo.split(":", 2);
                        if (username == null || username.isEmpty() || "postgres".equals(username)) {
                            username = parts[0];
                        }
                        if (password == null || password.isEmpty() || "PostgreSql-226".equals(password)) {
                            password = parts[1];
                        }
                    }

                    int port = uri.getPort() == -1 ? 5432 : uri.getPort();
                    String dbPath = uri.getPath();
                    String query = uri.getQuery();

                    trimmed = "jdbc:postgresql://" + uri.getHost() + ":" + port + dbPath;
                    if (query != null && !query.isEmpty()) {
                        trimmed += "?" + query;
                    }
                } catch (Exception e) {
                    if (trimmed.startsWith("postgres://")) {
                        trimmed = "jdbc:postgresql://" + trimmed.substring("postgres://".length());
                    } else if (trimmed.startsWith("postgresql://")) {
                        trimmed = "jdbc:" + trimmed;
                    }
                }
            } else if (!trimmed.startsWith("jdbc:")) {
                trimmed = "jdbc:" + trimmed;
            }

            rawUrl = trimmed;
        }

        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(rawUrl);
        if (username != null && !username.isEmpty()) {
            dataSource.setUsername(username);
        }
        if (password != null && !password.isEmpty()) {
            dataSource.setPassword(password);
        }
        dataSource.setDriverClassName("org.postgresql.Driver");
        return dataSource;
    }
}
