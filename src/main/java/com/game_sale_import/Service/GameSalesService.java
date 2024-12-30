package com.game_sale_import.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.game_sale_import.Model.GameSales;

@Service
public class GameSalesService {
	private static final Logger logger = LoggerFactory.getLogger(GameSalesService.class);
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	public static final RowMapper<GameSales> GAME_SALES_ROW_MAPPER = (rs, rowNum) -> {
	    GameSales gameSales = new GameSales();
	    gameSales.setId(rs.getInt("id"));
	    gameSales.setGameNo(rs.getInt("game_no"));
	    gameSales.setGameName(rs.getString("game_name"));
	    gameSales.setGameCode(rs.getString("game_code"));
	    gameSales.setType(rs.getInt("type"));
	    gameSales.setCostPrice(rs.getDouble("cost_price"));
	    gameSales.setTax(rs.getInt("tax"));
	    gameSales.setSalePrice(rs.getDouble("sale_price"));
	    gameSales.setDateOfSale(rs.getObject("date_of_sale", LocalDate.class));
	    gameSales.setCsvId(rs.getInt("csv_id"));
	    return gameSales;
	};
	
	//WITHOUT COUNT TRADE OFF
	public Page<GameSales> retrieveGameSales(
		    String fromDate, String toDate, BigDecimal price, Boolean lessThan, int page, int size) {

		    int offset = page * size;

		    StringBuilder sql = new StringBuilder("SELECT id, game_no, game_name, game_code, type, cost_price, tax, sale_price, date_of_sale, csv_id FROM game_sales_db.game_sales WHERE 1=1");
		    List<Object> params = new ArrayList<>();

		    if (fromDate != null && toDate != null) {
		        sql.append(" AND date_of_sale BETWEEN ? AND ?");
		        params.add(LocalDate.parse(fromDate));
		        params.add(LocalDate.parse(toDate));
		    }
		    if (price != null && lessThan != null) {
		        sql.append(lessThan ? " AND sale_price < ?" : " AND sale_price > ?");
		        params.add(price);
		    }

		    sql.append(" ORDER BY id ASC LIMIT ? OFFSET ?");
		    params.add(size + 1);
		    params.add(offset);

		    long startTime = System.currentTimeMillis();

		    List<GameSales> gameSalesList = jdbcTemplate.query(sql.toString(), params.toArray(), GAME_SALES_ROW_MAPPER);
		    long endTime = System.currentTimeMillis();
		    logger.info("Data query execution time: {}ms", endTime - startTime);

		    boolean hasNext = gameSalesList.size() > size;
		    if (hasNext) {
		        gameSalesList.remove(gameSalesList.size() - 1);
		    }
		    
		    logger.info("PageImpl totalElements: {}", hasNext ? -1 : gameSalesList.size());

		    return new PageImpl<>(gameSalesList, PageRequest.of(page, size), hasNext ? -1 : gameSalesList.size());
		}
	
	//WITH COUNT
	/*public Page<GameSales> retrieveGameSales(String fromDate, String toDate, BigDecimal price, Boolean lessThan, int page, int size) {
	    int offset = page * size;

	    String baseQuery = "FROM game_sales_db.game_sales WHERE 1=1";
	    StringBuilder sql = new StringBuilder("SELECT id, game_no, game_name, game_code, type, cost_price, tax, sale_price, date_of_sale, csv_id ");
	    sql.append(baseQuery);
	    StringBuilder countSql = new StringBuilder("SELECT COUNT(*) ").append(baseQuery);

	    List<Object> params = new ArrayList<>();
	    if (fromDate != null && toDate != null) {
	        sql.append(" AND date_of_sale BETWEEN ? AND ?");
	        countSql.append(" AND date_of_sale BETWEEN ? AND ?");
	        params.add(LocalDate.parse(fromDate));
	        params.add(LocalDate.parse(toDate));
	    }
	    if (price != null && lessThan != null) {
	        sql.append(lessThan ? " AND sale_price < ?" : " AND sale_price > ?");
	        countSql.append(lessThan ? " AND sale_price < ?" : " AND sale_price > ?");
	        params.add(price);
	    }
	    
	    if (params.isEmpty()) {
	        countSql.append(" LIMIT 1"); // Only fetch 1 result since you just need a count
	    }

	    sql.append(" ORDER BY id ASC LIMIT ? OFFSET ?");
	    params.add(size);
	    params.add(offset);

	    // Get total record count
	    long totalRecords = jdbcTemplate.queryForObject(countSql.toString(), params.subList(0, params.size() - 2).toArray(), Long.class);

	    // Fetch paginated data
	    List<GameSales> gameSalesList = jdbcTemplate.query(sql.toString(), params.toArray(), GAME_SALES_ROW_MAPPER);

	    return new PageImpl<>(gameSalesList, PageRequest.of(page, size), totalRecords);
	}*/
	
}
