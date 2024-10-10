package dev.bujiku.db.to.file.batch.processing;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * @author Newton Bujiku
 * @since 2024
 */
@Configuration
@RequiredArgsConstructor
public class ExportSalesBatchJobConfig {
    private final DataSource dataSource;

    public JdbcCursorItemReader<Sale> salesItemReader(@Value("#{jobParameters}") JobParameters parameters) {
        var processed = parameters.getParameters().get("processed");
        var sql = """
                SELECT `product_id`, `customer_id`, `sale_date`, `sale_amount`, `store_location`, `country`
                FROM `sales`
                WHERE `processed`=?1
                """;
        return new JdbcCursorItemReaderBuilder<Sale>()
                .name("salesReader")
                .dataSource(dataSource)
                .sql(sql)
                .fetchSize(100)
                .rowMapper(new SalesRowMapper())
                .queryArguments(processed)
                .build();
    }

}
