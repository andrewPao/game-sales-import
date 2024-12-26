package com.game_sale_import.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.game_sale_import.Model.GameSales;
import com.game_sale_import.Model.SaleSummary;
import com.game_sale_import.Utils.FormatDate;

@Service
public class SalesSummaryService {
	
	 private static final Logger logger = LoggerFactory.getLogger(SalesSummaryService.class);

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	public void aggregateAndSaveGameSales(List<GameSales> gameSalesList) {
		Map<String, SaleSummary> summaryMap = new HashMap<>();

		for (GameSales sale : gameSalesList) {		

			String key = sale.getGameNo() + "_" + sale.getDateOfSale();
			SaleSummary summary = summaryMap.getOrDefault(key, new SaleSummary(Integer.parseInt(sale.getGameNo()),FormatDate.localDateFormatDDMMYYYY(sale.getDateOfSale())));

			summary.setTotalNoOfGameSold(
				    (summary.getTotalNoOfGameSold() != null ? summary.getTotalNoOfGameSold() : 0) + 1
				);
			
			if (summary.getSale_price() == null) {
			    summary.setSale_price(BigDecimal.ZERO);
			}
			
			summary.setSale_price(summary.getSale_price().add(new BigDecimal(sale.getSalePrice())));
			summaryMap.put(key, summary);
		}

		List<SaleSummary> aggregatedSummaries = new ArrayList<>(summaryMap.values());
		mergeWithSaleSummary(aggregatedSummaries);
	}

	private void mergeWithSaleSummary(List<SaleSummary> saleSummaries) {
		
		logger.debug("Batch size: {}", saleSummaries.size());
		 final String sql = """
			        INSERT INTO game_sales_db.sale_summary (game_no, date_of_sale, total_no_of_game_sold, sale_price)
			        VALUES (?, ?, ?, ?)
			        ON DUPLICATE KEY UPDATE
			            total_no_of_game_sold = total_no_of_game_sold + VALUES(total_no_of_game_sold),
			            sale_price = sale_price + VALUES(sale_price);
			    """;

			    try {
			        
			        jdbcTemplate.batchUpdate(sql, saleSummaries, 1000000, (ps, summary) -> {
			            ps.setInt(1, summary.getGameNo());
			            ps.setObject(2, summary.getDateOfSale());
			            ps.setInt(3, summary.getTotalNoOfGameSold());
			            ps.setBigDecimal(4, summary.getSale_price());
			        });
			    } catch (Exception e) {
			        logger.error("An error occurred while performing batch update for SaleSummary.", e);
			    }
	}
}
