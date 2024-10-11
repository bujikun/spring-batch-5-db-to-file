package dev.bujiku.db.to.file.batch.processing;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Newton Bujiku
 * @since 2024
 */
public record TriggerInfo(
        int totalTriggerCount,
        boolean isRunForever,
        long repeatIntervalMills,
        Date startAt,
        String callbackData,
        String country
) implements Serializable {
}
