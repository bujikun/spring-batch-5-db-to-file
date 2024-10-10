package dev.bujiku.db.to.file.batch.processing;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author Newton Bujiku
 * @since 2024
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ExportSalesJobRunner {
    private final JobLauncher jobLauncher;
    private final Job readFromDBAndWriteToFileJob;

    //@EventListener(ApplicationReadyEvent.class)
    public void runJob() {
        var now = LocalDateTime.now().toString();
        var jobParameters = new JobParametersBuilder()
                .addLong("processed", 0L)
                .addString("filename", now + "-non-usa-sales.csv")
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
