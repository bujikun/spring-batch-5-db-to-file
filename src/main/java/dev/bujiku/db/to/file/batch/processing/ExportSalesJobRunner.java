package dev.bujiku.db.to.file.batch.processing;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.data.relational.core.sql.In;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 * @author Newton Bujiku
 * @since 2024
 */
//@Configuration
@RequiredArgsConstructor
@Slf4j
public class ExportSalesJobRunner {
    private final QuartzSchedulerService quartzSchedulerService;

//    @Bean
//    public CommandLineRunner commandLineRunner(){
//        return args -> {
//            var millsToStart = Instant.now().plus(30, ChronoUnit.SECONDS).toEpochMilli();
//            var triggerInfo = new TriggerInfo(4, false, 1000,
//                    new Date(System.currentTimeMillis()), "");
//            quartzSchedulerService.scheduleQuartzJob(QuartzJob.class, triggerInfo);
//        };
//    }

}
