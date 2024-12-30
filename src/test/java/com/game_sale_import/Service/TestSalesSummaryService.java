package com.game_sale_import.Service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.game_sale_import.Model.GameSales;
import com.game_sale_import.Model.SaleSummary;

public class TestSalesSummaryService {

	@Mock
	private JdbcTemplate jdbcTemplate;

	@InjectMocks
	private SalesSummaryService salesSummaryService;

	@Mock
	private SalesSummaryService spySalesSummaryService;

	@BeforeEach
	public void setup() {
		MockitoAnnotations.openMocks(this);
		spySalesSummaryService = spy(salesSummaryService);
	}

	@Test
	public void testAggregateAndSaveGameSales() {

		List<GameSales> gameSalesList = Arrays.asList(
				new GameSales(1, 101, "Game1", "G001", 1, 20.0, 5, 25.0, LocalDate.of(2024, 12, 27), 1),
				new GameSales(2, 101, "Game1", "G001", 1, 20.0, 5, 25.0, LocalDate.of(2024, 12, 27), 1),
				new GameSales(3, 102, "Game2", "G002", 2, 40.0, 10, 50.0, LocalDate.of(2024, 12, 28), 2));

		spySalesSummaryService.aggregateAndSaveGameSales(gameSalesList);

		verify(spySalesSummaryService, times(1)).mergeWithSaleSummary(argThat(summaries -> {
			if (summaries.size() != 2)
				return false;

			SaleSummary summary1 = summaries.get(0);
			if (summary1.getGameNo() != 101 || summary1.getTotalNoOfGameSold() != 2
					|| summary1.getSale_price().compareTo(new BigDecimal("50.0")) != 0)
				return false;

			SaleSummary summary2 = summaries.get(1);
			if (summary2.getGameNo() != 102 || summary2.getTotalNoOfGameSold() != 1
					|| summary2.getSale_price().compareTo(new BigDecimal("50.0")) != 0)
				return false;

			return true;
		}));
	}

	@Test
	public void testMergeWithSaleSummary() {

		List<SaleSummary> saleSummaries = List.of(
				new SaleSummary(10L, 123, "Test Game 1", LocalDate.of(2024, 12, 27), 10, BigDecimal.valueOf(250.0)),
				new SaleSummary(11L, 124, "Test Game 2", LocalDate.of(2024, 12, 28), 5, BigDecimal.valueOf(150.0)));

		final String sql = """
				    INSERT INTO game_sales_db.sale_summary (game_no, game_name, date_of_sale, total_no_of_game_sold, sale_price)
				    VALUES (?, ?, ?, ?, ?)
				    ON DUPLICATE KEY UPDATE
				        total_no_of_game_sold = total_no_of_game_sold + VALUES(total_no_of_game_sold),
				        sale_price = sale_price + VALUES(sale_price);
				""";

		salesSummaryService.mergeWithSaleSummary(saleSummaries);

		verify(jdbcTemplate, times(1)).batchUpdate(eq(sql), eq(saleSummaries), eq(1000000), any());
	}

	@Test
	public void testGetSaleSummary() {
	  
	    LocalDate startDate = LocalDate.of(2024, 12, 1);
	    LocalDate endDate = LocalDate.of(2024, 12, 31);
	    Integer gameNo = 101;

	
	    String expectedSql = "SELECT id, game_no, game_name, date_of_sale, total_no_of_game_sold, sale_price " +
	            "FROM game_sales_db.sale_summary WHERE date_of_sale BETWEEN ? AND ? AND game_no = ? ORDER BY id ASC";

	
	    List<SaleSummary> mockSaleSummaries = List.of(
	            new SaleSummary(1L, 101, "Test Game 1", LocalDate.of(2024, 12, 27), 10, BigDecimal.valueOf(250.0)),
	            new SaleSummary(2L, 101, "Test Game 2", LocalDate.of(2024, 12, 28), 15, BigDecimal.valueOf(500.0))
	    );

	   
	    doReturn(mockSaleSummaries).when(jdbcTemplate).query(
	            eq(expectedSql),
	            any(Object[].class),
	            any(RowMapper.class)
	    );

	 
	    List<SaleSummary> result = salesSummaryService.getSaleSummary(startDate, endDate, gameNo);

	    assertEquals(2, result.size());
	    assertEquals("Test Game 1", result.get(0).getGameName());
	    assertEquals(BigDecimal.valueOf(250.0), result.get(0).getSale_price());
	    assertEquals("Test Game 2", result.get(1).getGameName());
	    assertEquals(BigDecimal.valueOf(500.0), result.get(1).getSale_price());

	    verify(jdbcTemplate, times(1)).query(eq(expectedSql), any(Object[].class), any(RowMapper.class));
	}
}
