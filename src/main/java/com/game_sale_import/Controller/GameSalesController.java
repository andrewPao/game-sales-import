package com.game_sale_import.Controller;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
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
import com.game_sale_import.Repository.CsvImportProgressRepository;
import com.game_sale_import.Service.CsvImportService;
import com.game_sale_import.Service.GameSalesService;

@RestController
@RequestMapping("/gameSalesApi")
public class GameSalesController {

	@Autowired
	private CsvImportService importService;

	@Autowired
	private GameSalesService gameSalesService;

	// Q1
	@PostMapping("/import")
	public ResponseEntity<String> uploadCsv(@RequestParam("file") MultipartFile file) {
		try {
	
			CsvImportResult csvImportResponse = importService.importCsvAndInsert(file);
			

			return ResponseEntity.status(HttpStatus.MULTI_STATUS)
					.body("Import Status : " + csvImportResponse.getStatus() + " Total rows imported: " + csvImportResponse.getTotalRecords());
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
		}
	}
	

	// Qn3
	@GetMapping("/getGameSales")
	public ResponseEntity<Page<GameSales>> getGameSales(
			@RequestParam(value = "fromDate", required = false) String fromDate,
			@RequestParam(value = "toDate", required = false) String toDate,
			@RequestParam(value = "price", required = false) BigDecimal price,
			@RequestParam(value = "lessThan", required = false) Boolean lessThan,
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "size", defaultValue = "100") int size) {
		//Improvement plan, store in a global variable or cache the full list. Then stream lamda

		Pageable pageable = PageRequest.of(page, size);
		Page<GameSales> result = null;
		
		long startTime = System.currentTimeMillis();
		System.out.println("StartTime: " + startTime);

		if (fromDate != null && toDate != null) {
			result = gameSalesService.getGameSalesByDateRange(fromDate, toDate, pageable);
		} else if (price != null && lessThan != null) {
			result = gameSalesService.getGameSalesByPriceCondition(price, lessThan, pageable);
		} else {
			result = gameSalesService.getGameSales(pageable);
		}
		
		long endTime = System.currentTimeMillis();
		System.out.println("endTime: " + endTime);
		System.out.println("Retrieval Time: " + (endTime-startTime) + " milliseconds");

		return ResponseEntity.ok(result);
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
