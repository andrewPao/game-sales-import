package com.game_sale_import.Service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

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
import org.springframework.data.domain.Page;

import com.game_sale_import.Model.GameSales;

public class TestGameSalesService {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private GameSalesService gameSalesService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testRetrieveGameSales() {
    	
        String fromDate = "2024-01-01";
        String toDate = "2024-01-31";
        BigDecimal price = BigDecimal.valueOf(50);
        Boolean lessThan = true;
        int page = 0;
        int size = 10;

        List<GameSales> mockGameSalesList = Arrays.asList(
                new GameSales(1, 101, "Game1", "G001", 1, 25.0, 5, 30.0, LocalDate.of(2024, 1, 10), 1),
                new GameSales(2, 102, "Game2", "G002", 2, 40.0, 10, 45.0, LocalDate.of(2024, 1, 20), 1)
        );
        long mockTotalRecords = 2L;

        when(jdbcTemplate.queryForObject(anyString(), any(Object[].class), any(Class.class))).thenReturn(mockTotalRecords);
        when(jdbcTemplate.query(anyString(), any(Object[].class), any(GameSalesService.GAME_SALES_ROW_MAPPER.getClass())))
                .thenReturn(mockGameSalesList);

        Page<GameSales> result = gameSalesService.retrieveGameSales(fromDate, toDate, price, lessThan, page, size);

        assertEquals(mockTotalRecords, result.getTotalElements());
        assertEquals(mockGameSalesList.size(), result.getContent().size());
        assertEquals(mockGameSalesList.get(0).getGameName(), result.getContent().get(0).getGameName());

    }
}
