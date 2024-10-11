package dev.bujiku.db.to.file.batch.processing;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Newton Bujiku
 * @since 2024
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class QuartzSchedulerService {
    private final Scheduler scheduler;

    @Transactional(rollbackFor = Exception.class)
    public void scheduleQuartzJob(Class<? extends Job> jobClass, TriggerInfo triggerInfo) {
        var jobDetail = QuartzJobUtils.buildJobDetail(jobClass, triggerInfo);
        var trigger = QuartzJobUtils.buildTrigger(jobClass, triggerInfo);

        try {
            scheduler.scheduleJob(jobDetail, trigger);
            log.info("JOB SCHEDULED");
        } catch (SchedulerException e) {
            log.error("an error occurred while scheduling a job", e);
            throw new RuntimeException(e);
        }

    }

}
