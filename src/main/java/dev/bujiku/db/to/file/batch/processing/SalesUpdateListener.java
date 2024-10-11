package dev.bujiku.db.to.file.batch.processing;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ItemWriteListener;
import org.springframework.batch.item.Chunk;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

/**
 * @author Newton Bujiku
 * @since 2024
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class SalesUpdateListener implements ItemWriteListener<Sale> {
    private final JdbcClient jdbcClient;

    @Override
    public void afterWrite(Chunk<? extends Sale> items) {
        items.forEach(this::updateSaleProcessedStatus);
    }
    private void updateSaleProcessedStatus(Sale sale) {
        jdbcClient.sql("UPDATE sales SET processed=:processed WHERE sale_id=:saleId")
                .param("processed", 0)
//                .param("processed", 1)
                .param("saleId", sale.saleId())
                .update();
    }
}
