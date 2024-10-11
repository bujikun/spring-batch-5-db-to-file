package dev.bujiku.db.to.file.batch.processing;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.configuration.JobLocator;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author Newton Bujiku
 * @since 2024
 */
@Component
@PersistJobDataAfterExecution
@RequiredArgsConstructor
@Slf4j
public class QuartzJob extends QuartzJobBean {
    private final JobLauncher jobLauncher;
    private final JobLocator jobLocator;
    private final Job readFromDBAndWriteToFileJob;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        var now = LocalDateTime.now().toString();
        var country = String.valueOf(context.getMergedJobDataMap().get("country"));
        var jobParameters = new JobParametersBuilder()
                .addLong("processed", 0L)
                .addString("filename", "output/" + country + "-" + now + "-sales.csv")
                .addString("country", country)
                .addLocalDate("run on", LocalDate.now())
                .toJobParameters();

        try {
            jobLauncher.run(readFromDBAndWriteToFileJob, jobParameters);
        } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException |
                 JobParametersInvalidException e) {
            log.error("an error occurred while running {} due to {}", "readFromDBAndWriteToFileJob", e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
