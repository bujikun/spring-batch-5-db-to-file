package dev.bujiku.db.to.file.batch.processing;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
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
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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
    private final SalesUpdateListener salesUpdateListener;

//    @Bean
//    @StepScope
//    public JdbcCursorItemReader<Sale> salesItemReader(@Value("#{jobParameters}") Map<String, Object> parameters) {
//        var processed = parameters.get("processed");
//        var sql = """
//                SELECT sale_id, product_id, customer_id, sale_date, sale_amount, store_location, country,processed
//                FROM sales
//                WHERE processed=?
//                """;
//        return new JdbcCursorItemReaderBuilder<Sale>()
//                .name("salesReader")
//                .dataSource(dataSource)
//                .sql(sql)
//                .fetchSize(100)
//                .rowMapper(new SalesRowMapper())
//                .verifyCursorPosition(false)
//                .queryArguments(processed)
//                .build();
//    }

    @Bean
    @StepScope
    public JdbcPagingItemReader<Sale> alesItemReader(@Value("#{jobParameters}") Map<String, Object> parameters) {
        var processed = parameters.get("processed");
        var country = parameters.get("country");
        var selectClause = "SELECT sale_id, product_id, customer_id, sale_date, sale_amount, store_location, country,processed";
        var fromClause = "FROM sales";
        var whereClause = "WHERE processed=:processed AND country=:country";
        var paramMap = new HashMap<String, Object>();
        paramMap.put("processed", processed);
        paramMap.put("country", country);
        var parameterValues = Collections.unmodifiableMap(paramMap);
        return new JdbcPagingItemReaderBuilder<Sale>()
                .name("salesReader")
                .dataSource(dataSource)
                .rowMapper(new SalesRowMapper())
                .saveState(false)
                .selectClause(selectClause)
                .fromClause(fromClause)
                .whereClause(whereClause)
                .pageSize(300)
                .sortKeys(Collections.singletonMap("sale_id", Order.ASCENDING))
                .parameterValues(parameterValues)
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
                .names("product_id", "customer_id", "sale_date", "sale_amount", "store_location", "country", "processed")
                .shouldDeleteIfEmpty(true)
                .headerCallback(writer -> {
                    writer.append("sale_id, product_id, customer_id, sale_date, sale_amount, store_location, country,processed");
                })
                .saveState(false)
                .build();
    }

    @Bean
    public Step readFromDBAndWriteToFileStep(JdbcPagingItemReader<Sale> salesItemReader,
                                             FlatFileItemWriter<Sale> salesFlatFileItemWriter) {
        return new StepBuilder("readFromDBAndWriteToFileStep", jobRepository)
                .<Sale, Sale>chunk(600, platformTransactionManager)
                .reader(salesItemReader)
                .processor(new SalesItemProcessor())
                .writer(salesFlatFileItemWriter)
                //.faultTolerant()
                .listener(new SalesItemReadListener())
                .listener(salesUpdateListener)
                //.skipPolicy(new AlwaysSkipItemSkipPolicy())
                .taskExecutor(taskExecutor())
                .build();
    }

    private TaskExecutor taskExecutor() {
        var executor = new SimpleAsyncTaskExecutor();
        executor.setConcurrencyLimit(5000);
        executor.setVirtualThreads(true);
        return executor;
    }

    @Bean
    public Job readFromDBAndWriteToFileJob(Step readFromDBAndWriteToFileStep) {
        var now = LocalDateTime.now().toString();
        return new JobBuilder("readFromDBAndWriteToFileJob-" + now, jobRepository)
                .start(readFromDBAndWriteToFileStep)
                .incrementer(new RunIdIncrementer())
                .preventRestart()
                .build();
    }


}
