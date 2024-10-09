package dev.bujiku.db.to.file.batch.processing;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * @author Newton Bujiku
 * @since 2024
 */
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Table(name = "sales")
public class Sale {
    @Id
    private Long saleId;
    private Integer productId;
    private Integer customerId;
    private Instant saleDate;
    private BigDecimal saleAmount;
    private String storeLocation;
    private String country;
    private Boolean processed;

}