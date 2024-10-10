package dev.bujiku.db.to.file.batch.processing;

import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * @author Newton Bujiku
 * @since 2024
 */
@Getter
public record Sale(Long saleId,
                   Integer productId,
                   Integer customerId,
                   Instant saleDate,
                   BigDecimal saleAmount,
                   String storeLocation,
                   String country,
                   Boolean processed) {
}