package dev.bujiku.db.to.file.batch.processing;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @author Newton Bujiku
 * @since 2024
 */
public record Sale(Long saleId,
                   Integer productId,
                   Integer customerId,
                   LocalDate saleDate,
                   BigDecimal saleAmount,
                   String storeLocation,
                   String country,
                   Boolean processed) {
}