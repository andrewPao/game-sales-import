package com.game_sale_import.Controller;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.game_sale_import.Model.CsvImportResult;
import com.game_sale_import.Model.GameSales;
import com.game_sale_import.Model.SaleSummary;
import com.game_sale_import.Service.CsvImportService;
import com.game_sale_import.Service.GameSalesService;
import com.game_sale_import.Service.SalesSummaryService;

@RestController
@RequestMapping("/gameSalesApi")
public class GameSalesController {

	@Autowired
	private CsvImportService importService;
	
	@Autowired
	private GameSalesService gameSalesService;

	@Autowired
	private SalesSummaryService salesSummaryService;
	
	private static final Logger logger = LoggerFactory.getLogger(GameSalesController.class);
	

	// Q1
	@PostMapping("/import")
	public ResponseEntity<String> uploadCsv(@RequestParam("file") MultipartFile file) {
		try {
	
			CsvImportResult csvImportResponse = importService.importCsvAndInsert(file);

			return ResponseEntity.status(HttpStatus.OK)
					.body("Import Status : " + csvImportResponse.getStatus() + " Total rows imported: " + csvImportResponse.getTotalRecords());
		} catch (Exception e) {
			logger.error("Internal error occurred: {}", e.getMessage(), e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("APPLICATION UPDATE IN PROGRESS PLEASE TRY AGAIN LATER");
		}
	}
	
	// Qn3 
	@GetMapping("/getGameSales")
	public ResponseEntity<Object> getGameSales(
	        @RequestParam(value = "fromDate", required = false) String fromDate,
	        @RequestParam(value = "toDate", required = false) String toDate,
	        @RequestParam(value = "price", required = false) BigDecimal price,
	        @RequestParam(value = "lessThan", required = false) Boolean lessThan,
	        @RequestParam(value = "page", defaultValue = "0") int page,
	        @RequestParam(value = "size", defaultValue = "100") int size) {
	    try {
	     
	        LocalDate startDate = null;
	        LocalDate endDate = null;

	        if (fromDate != null) {
	            startDate = LocalDate.parse(fromDate); 
	        }
	        if (toDate != null) {
	            endDate = LocalDate.parse(toDate);
	        }

	        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                    .body(Map.of(
	                            "errorCode", "INVALID_DATE_RANGE",
	                            "message", "The 'fromDate' must be before 'toDate'."
	                    ));
	        }

	        long startTime = System.currentTimeMillis();
	        System.out.println("StartTime: " + startTime);

	        Page<GameSales> result = gameSalesService.retrieveGameSales(fromDate, toDate, price, lessThan, page, size);

	        long endTime = System.currentTimeMillis();
	        System.out.println("endTime: " + endTime);
	        System.out.println("Retrieval Time getGameSales: " + (endTime - startTime) + " milliseconds");

	        return ResponseEntity.ok(result);

	    } catch (DateTimeParseException e) {
	       
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                .body(Map.of(
	                        "errorCode", "INVALID_DATE_FORMAT",
	                        "message", "Invalid date format. Please use the format 'YYYY-MM-DD'."
	                ));
	    } catch (Exception e) {
	    	logger.error("Internal error occurred: {}", e.getMessage(), e);
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body(Map.of(
	                        "errorCode", "SYSTEM_ERROR",
	                        "message", "APPLICATION UPDATE IN PROGRESS PLEASE TRY AGAIN LATER"
	                ));
	    }
	}
	
	// Qn4
	@GetMapping("/getTotalSales")
	public ResponseEntity<Object> getTotalSales(
	        @RequestParam String fromDate,
	        @RequestParam(required = false) String toDate,
	        @RequestParam(required = false) Integer gameNo) {
	    try {
	        
	        LocalDate startDate = LocalDate.parse(fromDate);
	        LocalDate endDate = toDate == null || toDate.isEmpty() ? startDate : LocalDate.parse(toDate);

	        if (startDate.isAfter(endDate)) {
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                    .body(Map.of(
	                            "errorCode", "INVALID_DATE_RANGE",
	                            "message", "'fromDate' must not be after 'toDate'."
	                    ));
	        }

	        long startTime = System.currentTimeMillis();
	        System.out.println("StartTime: " + startTime);

	        List<SaleSummary> saleSummaries = salesSummaryService.getSaleSummary(startDate, endDate, gameNo);

	        long endTime = System.currentTimeMillis();
	        System.out.println("endTime: " + endTime);
	        System.out.println("Retrieval Time getTotalSales: " + (endTime - startTime) + " milliseconds");

	        return ResponseEntity.ok(saleSummaries);

	    } catch (DateTimeParseException e) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                .body(Map.of(
	                        "errorCode", "INVALID_DATE_FORMAT",
	                        "message", "Invalid date format. Please use the format 'YYYY-MM-DD'."
	                ));
	    } catch (Exception e) {
	        logger.error("Internal error occurred: {}", e.getMessage(), e);
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body(Map.of(
	                        "errorCode", "SYSTEM_ERROR",
	                        "message", "An unexpected error occurred while retrieving sales data."
	                ));
	    }
	}
	
	@PostMapping("/generateCsvFile")
	public void generateCsvFileWithData() {

		File outputDir = new File("target/output");
		if (!outputDir.exists()) {
			outputDir.mkdirs();
		}

		String fileName = "target/output/game_sales_data.csv";
		int numberOfRows = 1000000;

		try (FileWriter writer = new FileWriter(fileName);
				CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader("id", "gameNo",
						"gameName", "gameCode", "type", "costPrice", "tax", "salePrice", "dateOfSale"))) {

			long startTime = System.currentTimeMillis();
			System.out.println("CSV file started to generate...");
			generateData(csvPrinter, numberOfRows);

			long endTime = System.currentTimeMillis();

			System.out.println("Time taken: " + (endTime - startTime) + " ms");
			System.out.println("CSV file generated successfully: " + fileName);

		} catch (IOException e) {
			System.err.println("Error writing CSV file: " + e.getMessage());
		}
	}

	public static void generateData(CSVPrinter csvPrinter, int numberOfRows) throws IOException {
		Random random = new Random();
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

		LocalDate startDate = LocalDate.of(2024, 4, 1);
		LocalDate endDate = LocalDate.of(2024, 4, 30);

		for (int i = 1; i <= numberOfRows; i++) {
			int id = i;
			int gameNo = random.nextInt(100) + 1;
			String gameName = "Game" + random.nextInt(10000);
			String gameCode = String.format("%05d", random.nextInt(100000));
			String type = random.nextBoolean() ? "1" : "2";
			double costPrice = random.nextDouble() * 100;
			int tax = 9;
			double salePrice = costPrice + (costPrice * tax / 100);
			String dateOfSale = generateRandomDate(random, startDate, endDate).format(dateFormatter);

			csvPrinter.printRecord(id, gameNo, gameName, gameCode, type, String.format("%.2f", costPrice), tax,
					String.format("%.2f", salePrice), dateOfSale);
		}

	}

	private static LocalDate generateRandomDate(Random random, LocalDate startDate, LocalDate endDate) {
		long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);
		return startDate.plusDays(random.nextInt((int) daysBetween + 1));
	}
	

}
