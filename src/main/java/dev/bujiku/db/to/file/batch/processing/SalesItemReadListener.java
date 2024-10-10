package dev.bujiku.db.to.file.batch.processing;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ItemReadListener;

/**
 * @author Newton Bujiku
 * @since 2024
 */
@Slf4j
public class SalesItemReadListener implements ItemReadListener<Sale> {

    @Override
    public void afterRead(Sale item) {
       // log.info("AFTER READ");
        //ItemReadListener.super.afterRead(item);

    }

    @Override
    public void onReadError(Exception ex) {
        //log.info("ERROR ON READ");
        //ItemReadListener.super.onReadError(ex);
    }
}
