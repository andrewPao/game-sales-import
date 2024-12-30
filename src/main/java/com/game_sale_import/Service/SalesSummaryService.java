package com.game_sale_import.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.game_sale_import.Model.GameSales;
import com.game_sale_import.Model.SaleSummary;

@Service
public class SalesSummaryService {
	
	private static final Logger logger = LoggerFactory.getLogger(SalesSummaryService.class);

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	public void aggregateAndSaveGameSales(List<GameSales> gameSalesList) {
		Map<String, SaleSummary> summaryMap = new HashMap<>();

		for (GameSales sale : gameSalesList) {		

			String key = sale.getGameNo() + "_" + sale.getDateOfSale();
			SaleSummary summary = summaryMap.getOrDefault(key, new SaleSummary(sale.getGameNo(),sale.getDateOfSale()));

			summary.setTotalNoOfGameSold(
				    (summary.getTotalNoOfGameSold() != null ? summary.getTotalNoOfGameSold() : 0) + 1
				);
			
			if (summary.getSale_price() == null) {
			    summary.setSale_price(BigDecimal.ZERO);
			}
			
			summary.setSale_price(summary.getSale_price().add(new BigDecimal(sale.getSalePrice())));
			summary.setGameName(sale.getGameName());
			summaryMap.put(key, summary);
		}

		List<SaleSummary> aggregatedSummaries = new ArrayList<>(summaryMap.values());
		mergeWithSaleSummary(aggregatedSummaries);
	}

	public void mergeWithSaleSummary(List<SaleSummary> saleSummaries) {
		
		logger.debug("Batch size: {}", saleSummaries.size());
		 final String sql = """
			        INSERT INTO game_sales_db.sale_summary (game_no, game_name, date_of_sale, total_no_of_game_sold, sale_price)
			        VALUES (?, ?, ?, ?, ?)
			        ON DUPLICATE KEY UPDATE
			            total_no_of_game_sold = total_no_of_game_sold + VALUES(total_no_of_game_sold),
			            sale_price = sale_price + VALUES(sale_price);
			    """;

			    try {
			        
			        jdbcTemplate.batchUpdate(sql, saleSummaries, 1000000, (ps, summary) -> {
			            ps.setInt(1, summary.getGameNo());
			            ps.setString(2, summary.getGameName());
			            ps.setObject(3, summary.getDateOfSale());
			            ps.setInt(4, summary.getTotalNoOfGameSold());
			            ps.setBigDecimal(5, summary.getSale_price());
			        });
			    } catch (Exception e) {
			        logger.error("An error occurred while performing batch update for SaleSummary.", e);
			    }
	}
	
	public List<SaleSummary> getSaleSummary(LocalDate start, LocalDate end, Integer gameNo) {
	    String baseQuery = "FROM game_sales_db.sale_summary WHERE date_of_sale BETWEEN ? AND ?";
	    StringBuilder sql = new StringBuilder("SELECT id, game_no, game_name, date_of_sale, total_no_of_game_sold, sale_price ");
	    sql.append(baseQuery);

	    List<Object> params = new ArrayList<>();
	    params.add(start);
	    params.add(end);

	    if (gameNo != null) {
	        sql.append(" AND game_no = ?");
	        params.add(gameNo);
	    }
	    
	    sql.append(" ORDER BY id ASC");

	    return jdbcTemplate.query(sql.toString(), params.toArray(), (rs, rowNum) -> {
	        SaleSummary summary = new SaleSummary();
	        summary.setId(rs.getLong("id"));
	        summary.setGameNo(rs.getInt("game_no"));
	        summary.setGameName(rs.getString("game_name"));
	        summary.setDateOfSale(rs.getDate("date_of_sale").toLocalDate());
	        summary.setTotalNoOfGameSold(rs.getInt("total_no_of_game_sold"));
	        summary.setSale_price(rs.getBigDecimal("sale_price"));
	        return summary;
	    });
	}


}
