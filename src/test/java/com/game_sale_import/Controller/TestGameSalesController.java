package com.game_sale_import.Controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.game_sale_import.Model.CsvImportResult;
import com.game_sale_import.Model.GameSales;
import com.game_sale_import.Model.SaleSummary;
import com.game_sale_import.Service.CsvImportService;
import com.game_sale_import.Service.GameSalesService;
import com.game_sale_import.Service.SalesSummaryService;

@WebMvcTest(GameSalesController.class)
public class TestGameSalesController {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private CsvImportService csvImportService;

	@MockBean
	private GameSalesService gameSalesService;

	@MockBean
	private SalesSummaryService salesSummaryService;

	@Test
	public void testUploadCsv_Success() throws Exception {

		MockMultipartFile file = new MockMultipartFile("file", "test.csv", MediaType.TEXT_PLAIN_VALUE,
				("id,game_no,game_name,game_code,type,cost_pr,tax,sale_price,date_of_sale\n"
						+ "1,14,mario,ABC12,1,20,9,21.8,17/12/2024\n" + "2,15,sonic,DEF23,2,30,9,32.7,17/12/2024\n"
						+ "3,16,superduper,xxx12,1,30,9,32.7,17/12/2024\n").getBytes());

		CsvImportResult mockResult = new CsvImportResult("COMPLETED", 3);
		when(csvImportService.importCsvAndInsert(file)).thenReturn(mockResult);

		mockMvc.perform(multipart("/gameSalesApi/import").file(file)).andExpect(status().isOk())
				.andExpect(content().string("Import Status : COMPLETED Total rows imported: 3"));
	}

	@Test
	public void testUploadCsv_Error() throws Exception {
		MockMultipartFile file = new MockMultipartFile("file", "test.csv", MediaType.TEXT_PLAIN_VALUE,
				("id,game_no,game_name,game_code,type,cost_pr,tax,sale_price,date_of_sale\n"
						+ "1,14,mario,ABC12,1,20,9,21.8,17/12/2024\n" + "2,15,sonic,DEF23,2,30,9,32.7,17/12/2024\n"
						+ "3,16,superduper,xxx12,1,30,9,32.7,17/12/2024\n").getBytes());

		when(csvImportService.importCsvAndInsert(file)).thenThrow(new RuntimeException("Mock exception"));

		mockMvc.perform(multipart("/gameSalesApi/import").file(file)).andExpect(status().isInternalServerError())
				.andExpect(content().string("APPLICATION UPDATE IN PROGRESS PLEASE TRY AGAIN LATER"));
	}

	@Test
	public void testGetGameSales_ValidInput() throws Exception {
		GameSales mockGameSales = new GameSales(1, 1001, "Testing", "Test01", 1, 50.0, 10, 60.0,
				LocalDate.of(2024, 1, 1), 101);

		Page<GameSales> mockPage = new PageImpl<>(List.of(mockGameSales), PageRequest.of(0, 100), 1);

		when(gameSalesService.retrieveGameSales(anyString(), anyString(), any(), anyBoolean(), anyInt(), anyInt()))
				.thenReturn(mockPage);

		mockMvc.perform(get("/gameSalesApi/getGameSales").param("fromDate", "2024-01-01").param("toDate", "2024-12-31")
				.param("page", "0").param("size", "100")).andExpect(status().isOk());

		assertEquals(1, mockPage.getContent().get(0).getId());
		assertEquals("Testing", mockPage.getContent().get(0).getGameName());
	}

	@Test
	public void testGetGameSales_InvalidDateRange() throws Exception {
		String response = mockMvc
				.perform(get("/gameSalesApi/getGameSales").param("fromDate", "2024-12-31").param("toDate", "2024-01-01")
						.param("page", "0").param("size", "100"))
				.andExpect(status().isBadRequest()).andReturn().getResponse().getContentAsString();

		ObjectMapper objectMapper = new ObjectMapper();
		Map<String, String> errorResponse = objectMapper.readValue(response, new TypeReference<>() {
		});
		assertEquals("INVALID_DATE_RANGE", errorResponse.get("errorCode"));
		assertEquals("The 'fromDate' must be before 'toDate'.", errorResponse.get("message"));
	}
	
	 @Test
	    public void testGetGameSales_InvalidDateFormat() throws Exception {
	        String response = mockMvc.perform(get("/gameSalesApi/getGameSales")
	                        .param("fromDate", "invalid-date")
	                        .param("toDate", "2024-12-31")
	                        .param("page", "0")
	                        .param("size", "100"))
	                .andExpect(status().isBadRequest())
	                .andReturn()
	                .getResponse()
	                .getContentAsString();

	        ObjectMapper objectMapper = new ObjectMapper();
	        Map<String, String> errorResponse = objectMapper.readValue(response, new TypeReference<>() {});
	        assertEquals("INVALID_DATE_FORMAT", errorResponse.get("errorCode"));
	        assertEquals("Invalid date format. Please use the format 'YYYY-MM-DD'.", errorResponse.get("message"));
	    }
	 
	 @Test
	    public void testGetGameSales_InternalServerError() throws Exception {
		 when(gameSalesService.retrieveGameSales("2024-01-01", "2024-12-31", null, null, 0, 100))
	        .thenThrow(new RuntimeException("Mock exception"));

	        String response = mockMvc.perform(get("/gameSalesApi/getGameSales")
	                        .param("fromDate", "2024-01-01")
	                        .param("toDate", "2024-12-31")
	                        .param("page", "0")
	                        .param("size", "100"))
	        		.andDo(print())
	                .andExpect(status().isInternalServerError())
	                .andReturn()
	                .getResponse()
	                .getContentAsString();

	        ObjectMapper objectMapper = new ObjectMapper();
	        Map<String, String> errorResponse = objectMapper.readValue(response, new TypeReference<>() {});
	        assertEquals("SYSTEM_ERROR", errorResponse.get("errorCode"));
	        assertEquals("APPLICATION UPDATE IN PROGRESS PLEASE TRY AGAIN LATER", errorResponse.get("message"));
	    }

	 @Test
	 public void testGetTotalSales_ValidInput() throws Exception {
	     List<SaleSummary> mockSummaries = List.of(
	             new SaleSummary(1L, 123, "Testing01", LocalDate.of(2024, 1, 15), 50, BigDecimal.valueOf(500.0))
	     );

	     when(salesSummaryService.getSaleSummary(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 31), 123))
	             .thenReturn(mockSummaries);

	     mockMvc.perform(get("/gameSalesApi/getTotalSales")
	                     .param("fromDate", "2024-01-01")
	                     .param("toDate", "2024-01-31")
	                     .param("gameNo", "123"))
	             .andExpect(status().isOk());
	             
	         	assertEquals(1, mockSummaries.get(0).getId());
	     		assertEquals("Testing01", mockSummaries.get(0).getGameName());
	     		
	 }
	 
	 @Test
	 public void testGetTotalSales_InvalidDateRange() throws Exception {
	     String response = mockMvc.perform(get("/gameSalesApi/getTotalSales")
	                     .param("fromDate", "2024-01-31")
	                     .param("toDate", "2024-01-01")
	                     .param("gameNo", "123"))
	             .andExpect(status().isBadRequest())
	             .andReturn()
	             .getResponse()
	             .getContentAsString();

	     ObjectMapper objectMapper = new ObjectMapper();
	     Map<String, String> errorResponse = objectMapper.readValue(response, new TypeReference<>() {});
	     assertEquals("INVALID_DATE_RANGE", errorResponse.get("errorCode"));
	     assertEquals("'fromDate' must not be after 'toDate'.", errorResponse.get("message"));
	 }
	 
	 @Test
	 public void testGetTotalSales_InvalidDateFormat() throws Exception {
	     String response = mockMvc.perform(get("/gameSalesApi/getTotalSales")
	                     .param("fromDate", "invalid-date")
	                     .param("toDate", "2024-01-31")
	                     .param("gameNo", "123"))
	             .andExpect(status().isBadRequest())
	             .andReturn()
	             .getResponse()
	             .getContentAsString();

	     ObjectMapper objectMapper = new ObjectMapper();
	     Map<String, String> errorResponse = objectMapper.readValue(response, new TypeReference<>() {});
	     assertEquals("INVALID_DATE_FORMAT", errorResponse.get("errorCode"));
	     assertEquals("Invalid date format. Please use the format 'YYYY-MM-DD'.", errorResponse.get("message"));
	 }
	 
	 @Test
	 public void testGetTotalSales_InternalServerError() throws Exception {
	     when(salesSummaryService.getSaleSummary(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 31), 123))
	             .thenThrow(new RuntimeException("Mock exception"));

	     String response = mockMvc.perform(get("/gameSalesApi/getTotalSales")
	                     .param("fromDate", "2024-01-01")
	                     .param("toDate", "2024-01-31")
	                     .param("gameNo", "123"))
	             .andExpect(status().isInternalServerError())
	             .andReturn()
	             .getResponse()
	             .getContentAsString();

	     ObjectMapper objectMapper = new ObjectMapper();
	     Map<String, String> errorResponse = objectMapper.readValue(response, new TypeReference<>() {});
	     assertEquals("SYSTEM_ERROR", errorResponse.get("errorCode"));
	     assertEquals("An unexpected error occurred while retrieving sales data.", errorResponse.get("message"));
	 }
}
