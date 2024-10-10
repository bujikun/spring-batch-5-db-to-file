package dev.bujiku.db.to.file.batch.processing;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

/**
 * @author Newton Bujiku
 * @since 2024
 */
@Slf4j
public class SalesItemProcessor implements ItemProcessor<Sale, Sale> {
    @Override
    @Nullable
    public Sale process(@NonNull Sale item) throws Exception {
       // log.info("processing item : {}", item);
        if (item.country().equalsIgnoreCase("United States")) {
            return null;
        }
        return item;
    }
}
