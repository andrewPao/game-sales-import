package com.game_sale_import.Service;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockMultipartFile;

import com.game_sale_import.Model.CsvImportProgress;
import com.game_sale_import.Model.CsvImportResult;
import com.game_sale_import.Model.GameSales;
import com.game_sale_import.Repository.CsvImportProgressRepository;

public class TestCsvImportService {
	
    @Mock
    private JdbcTemplate jdbcTemplate;

    @Mock
    private CsvImportProgressRepository progressRepository;

    @Mock
    private SalesSummaryService salesSummaryService;

    @InjectMocks
    private CsvImportService csvImportService;
    
    private static final String sql = "INSERT INTO game_sales (id, game_no, game_name, game_code, type, cost_price, tax, sale_price, date_of_sale, csv_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }
    
    @Test
    public void testImportCsvAndInsert_Success() throws Exception {
    	String csvContent = "id,game_no,game_name,game_code,type,cost_price,tax,sale_price,date_of_sale,csv_id\n" +
                "1,10,ValidGame,ABC12,1,99.99,10,109.99,27/12/2024,1\n" +
                "2,15,AnotherValidGame,XYZ34,2,50.50,8,58.54,15/12/2024,2";
    	 	MockMultipartFile file = new MockMultipartFile("file", "test.csv", "text/csv", csvContent.getBytes());

			CsvImportProgress mockProgress = new CsvImportProgress();
			mockProgress.setId(1);
			mockProgress.setFileName("test.csv");
			mockProgress.setStartTime(LocalDateTime.now());
			mockProgress.setStatus("PROCESSING");
			when(progressRepository.save(any(CsvImportProgress.class))).thenReturn(mockProgress);
			
			doNothing().when(salesSummaryService).aggregateAndSaveGameSales(anyList());
			
			CsvImportResult result = csvImportService.importCsvAndInsert(file);
			
			assertEquals("COMPLETED", result.getStatus());
			assertEquals(2, result.getTotalRecords());
			
    }
    
    @Test
    public void testReadCsvData_Success() throws Exception {
    	String csvContent = "id,game_no,game_name,game_code,type,cost_price,tax,sale_price,date_of_sale,csv_id\n" +
                "1,10,TestGame1,ABC12,1,20.5,5,21.0,27/12/2024,1\n" +
                "2,20,AnotherGame2,XYZ99,2,50.0,10,60.0,28/12/2024,2\n" +
                "3,30,ValidGame3,GME33,1,75.5,8,80.0,29/12/2024,3";
        MockMultipartFile file = new MockMultipartFile("file", "test.csv", "text/csv", csvContent.getBytes());

        CsvImportProgress progress = new CsvImportProgress();
        progress.setId(1);
        progress.setFileName("test.csv");
        progress.setStartTime(LocalDateTime.now());
        progress.setStatus("PROCESSING");

        List<GameSales> gameSalesList = csvImportService.readCsvData(file, progress);

        assertEquals(3, gameSalesList.size());
        assertEquals("TestGame1", gameSalesList.get(0).getGameName());
    }
    
    @Test
    void testBatchInsertGameSales() {
        CsvImportProgress mockProgress = new CsvImportProgress();
        mockProgress.setId(1);

        GameSales gameSale = new GameSales(1,10,"TestGame","ABC12",1,20.5,5,21.0,LocalDate.of(2024, 12, 27),1);
        List<GameSales> gameSalesList = List.of(gameSale);
        when(jdbcTemplate.batchUpdate(eq(sql), eq(gameSalesList), eq(1000000), any())).thenReturn(null);
        csvImportService.batchInsertGameSales(mockProgress, gameSalesList);

        verify(jdbcTemplate, times(1)).batchUpdate(eq(sql), eq(gameSalesList), eq(1000000), any());
    }
    
    @Test
    void testSaveInitialProgress_Success() throws IOException {
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "test.csv",
            "text/csv",
            "Sample CSV Content".getBytes()
        );

        CsvImportProgress mockProgress = new CsvImportProgress();
        mockProgress.setId(1);
        mockProgress.setFileName("test.csv");
        mockProgress.setFileData("Sample CSV Content".getBytes());
        mockProgress.setStartTime(LocalDateTime.now());
        mockProgress.setStatus("PROCESSING");

        when(progressRepository.save(any(CsvImportProgress.class))).thenReturn(mockProgress);

        CsvImportProgress result = csvImportService.saveInitialProgress(file);

        assertNotNull(result);
        assertEquals("test.csv", result.getFileName());
        assertArrayEquals("Sample CSV Content".getBytes(), result.getFileData());
        assertEquals("PROCESSING", result.getStatus());

        verify(progressRepository, times(1)).save(any(CsvImportProgress.class));
    }

}
