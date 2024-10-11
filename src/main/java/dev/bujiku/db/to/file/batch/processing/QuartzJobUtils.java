package dev.bujiku.db.to.file.batch.processing;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.quartz.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * @author Newton Bujiku
 * @since 2024
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class QuartzJobUtils {

    public static JobDetail buildJobDetail(Class<? extends Job> quartzJobClass, TriggerInfo triggerInfo) {
        var now = Instant.now().truncatedTo(ChronoUnit.SECONDS).toString();
        var jobDataMap = new JobDataMap();
        var jobName = quartzJobClass.getName() + "-" + now + "-" + triggerInfo.country();
        jobDataMap.put(jobName, triggerInfo);
        jobDataMap.put("country", triggerInfo.country());
        return JobBuilder
                .newJob(quartzJobClass)
                .withIdentity(jobName)
                .setJobData(jobDataMap)
                .storeDurably(true)
                .build();
    }

    public static Trigger buildTrigger(Class<? extends Job> quartzJobClass, TriggerInfo triggerInfo) {
        var now = Instant.now().truncatedTo(ChronoUnit.SECONDS).toString();
        var scheduleBuilder = SimpleScheduleBuilder
                .simpleSchedule()
                .withIntervalInMilliseconds(triggerInfo.repeatIntervalMills());
        if (triggerInfo.isRunForever()) {
            scheduleBuilder.repeatForever();
        } else {
            scheduleBuilder = scheduleBuilder.withRepeatCount(triggerInfo.totalTriggerCount() - 1);
        }
        var jobName = quartzJobClass.getName() + "-" + now + "-" + triggerInfo.country();
        return TriggerBuilder
                .newTrigger()
                .withIdentity(jobName)
                .withSchedule(scheduleBuilder)
                // .startAt(new Date())
                .startAt(triggerInfo.startAt())
                .build();
    }

}
