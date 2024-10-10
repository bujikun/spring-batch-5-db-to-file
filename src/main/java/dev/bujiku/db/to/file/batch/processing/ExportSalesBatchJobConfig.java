package dev.bujiku.db.to.file.batch.processing;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.skip.AlwaysSkipItemSkipPolicy;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.file.FlatFileHeaderCallback;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.Writer;

/**
 * @author Newton Bujiku
 * @since 2024
 */
@Configuration
@RequiredArgsConstructor
public class ExportSalesBatchJobConfig {
    private final DataSource dataSource;
    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;

    @Bean
    @StepScope
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

    @Bean
    @StepScope
    public FlatFileItemWriter<Sale> salesFlatFileItemWriter(@Value("#{jobParameters['filename']}") String filename) {

        return new FlatFileItemWriterBuilder<Sale>()
                .name("salesWriter")
                .resource(new FileSystemResource(filename))
                .delimited()
                .delimiter(",")
                .sourceType(Sale.class)
                .names("product_id", "customer_id", "sale_date", "sale_amount", "store_location", "country")
                .shouldDeleteIfEmpty(true)
                .headerCallback(writer -> {
                    writer.append("Header");
                })
                .build();
    }

    @Bean
    public Step step(JdbcCursorItemReader<Sale> salesItemReader, FlatFileItemWriter<Sale> salesFlatFileItemWriter) {
        return new StepBuilder("readFromDBAndWriteToFile", jobRepository)
                .<Sale, Sale>chunk(200, platformTransactionManager)
                .reader(salesItemReader)
                .processor(new SalesItemProcessor())
                .writer(salesFlatFileItemWriter)
                .faultTolerant()
                .skipPolicy(new AlwaysSkipItemSkipPolicy())
                .taskExecutor(taskExecutor())
                .build();
    }

    private TaskExecutor taskExecutor() {
        var executor = new SimpleAsyncTaskExecutor();
        executor.setConcurrencyLimit(100);
        executor.setVirtualThreads(true);
        return executor;
    }

}
