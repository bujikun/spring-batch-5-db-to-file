package dev.bujiku.db.to.file.batch.processing;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.quartz.QuartzDataSource;
import org.springframework.boot.autoconfigure.quartz.QuartzTransactionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

/**
 * @author Newton Bujiku
 * @since 2024
 */
@Configuration
@Slf4j
public class QuartzConfig {
    private final String username;
    private final String password;
    private final String jdbcUrl;


    public QuartzConfig(@Value("${spring.datasource.username}") String username,
                        @Value("${spring.datasource.password}") String password,
                        @Value("${spring.datasource.url}") String jdbcUrl
    ) {
        this.username = username;
        this.password = password;
        this.jdbcUrl = jdbcUrl;

    }

    @Bean
    @QuartzDataSource
    public DataSource quartzDataSource() {

        var hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName("org.mariadb.jdbc.Driver");
        hikariConfig.setConnectionTimeout(30000);
        hikariConfig.setMaxLifetime(1800000);
        hikariConfig.setMaximumPoolSize(25);
        hikariConfig.setIdleTimeout(600000);
        hikariConfig.setConnectionTestQuery("SELECT 1");
        hikariConfig.setAutoCommit(false);
        hikariConfig.setJdbcUrl(jdbcUrl);
        hikariConfig.setUsername(username);
        hikariConfig.setPassword(password);
        return new HikariDataSource(hikariConfig);
    }

    @Bean
    @QuartzTransactionManager
    public PlatformTransactionManager quartzTransactionManager(DataSource quartzDataSource) {
        return new DataSourceTransactionManager(quartzDataSource);
    }


}
