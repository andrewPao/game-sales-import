package com.game_sale_import.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.game_sale_import.Model.GameSales;
import com.game_sale_import.Model.GameSalesId;

public interface GameSalesRepository extends JpaRepository<GameSales,GameSalesId>{
	
	@Query(value = "SELECT * FROM game_sales_db.game_sales " +
            "WHERE STR_TO_DATE(date_of_sale, '%d/%m/%Y') BETWEEN STR_TO_DATE(:fromDate, '%d/%m/%Y') " +
            "AND STR_TO_DATE(:toDate, '%d/%m/%Y')", 
    nativeQuery = true)
	Page<GameSales> findByDateOfSaleBetween(@Param("fromDate") String fromDate, @Param("toDate") String toDate, Pageable pageable);
	
	@Query(value = "SELECT * FROM game_sales_db.game_sales WHERE CAST(sale_price AS DECIMAL(10,2)) < :price", nativeQuery = true)
    Page<GameSales> findBySalePriceLessThan(BigDecimal price, Pageable pageable);
	
	@Query(value = "SELECT * FROM game_sales_db.game_sales WHERE CAST(sale_price AS DECIMAL(10,2)) > :price", nativeQuery = true)
    Page<GameSales> findBySalePriceGreaterThan(BigDecimal price, Pageable pageable);

}
