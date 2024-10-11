package dev.bujiku.db.to.file.batch.processing;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 * @author Newton Bujiku
 * @since 2024
 */
@RestController
@RequiredArgsConstructor
public class QuartzJobController {
    private final QuartzJob quartzJob;
    private final QuartzSchedulerService quartzSchedulerService;

    @PostMapping("/schedule-job")
    public void scheduleJob(@RequestBody ReqBody reqBody) {
//        var millsToStart = Instant.now().plus(60, ChronoUnit.SECONDS).toEpochMilli();
        var millsToStart = Instant.now().plus(3, ChronoUnit.HOURS).toEpochMilli();
        var triggerInfo = new TriggerInfo(1, false, 0,
                new Date(millsToStart), "", reqBody.country());
        quartzSchedulerService.scheduleQuartzJob(QuartzJob.class, triggerInfo);
    }


}
