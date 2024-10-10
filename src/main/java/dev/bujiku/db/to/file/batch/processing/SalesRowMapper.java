package dev.bujiku.db.to.file.batch.processing;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Newton Bujiku
 * @since 2024
 */
public class SalesRowMapper implements RowMapper<Sale> {
    @Override
    public Sale mapRow(ResultSet rs, int rowNum) throws SQLException {
        var saleDate = rs.getDate("sale_date").toLocalDate();
        var saleAmount = rs.getBigDecimal("sale_amount");
        //no need to catch exceptions
        return new Sale(rs.getLong("sale_id"),
                rs.getInt("product_id"),
                rs.getInt("customer_id"),
                saleDate,
                saleAmount,
                rs.getString("store_location"),
                rs.getString("country"),
                rs.getBoolean("processed")
        );
    }
}
