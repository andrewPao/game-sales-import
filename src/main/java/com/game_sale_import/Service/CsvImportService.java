package com.game_sale_import.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.game_sale_import.Model.CsvImportProgress;
import com.game_sale_import.Model.CsvImportResult;
import com.game_sale_import.Model.GameSales;
import com.game_sale_import.Repository.CsvImportProgressRepository;
import com.game_sale_import.Updater.ProgressUpdater;
import com.game_sale_import.Validator.GameSalesValidator;
import com.opencsv.CSVReader;

@Service
public class CsvImportService {
	
	@Autowired
	private DataSource dataSource;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private CsvImportProgressRepository progressRepository;
	
	@Autowired
	private SalesSummaryService salesSummaryService;
	

	private static final String sql = "INSERT INTO game_sales (id, game_no, game_name, game_code, type, cost_price, tax, sale_price, date_of_sale, csv_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

	@Transactional
	public CsvImportResult importCsvAndInsert(MultipartFile file) throws Exception {

		CsvImportProgress progress = saveInitialProgress(file);

		List<GameSales> gameSalesList = readCsvData(file, progress);

		if (!gameSalesList.isEmpty()) {
			try {

				batchInsertGameSales(gameSalesList);
				ProgressUpdater.updateProgress(progress, "COMPLETED", gameSalesList.size(), null, progressRepository);

				return new CsvImportResult("COMPLETED", gameSalesList.size());

			} catch (DataAccessException e) {
				ProgressUpdater.updateProgress(progress, "SYSTEM_ERROR", 0, List.of("System error: " + e.getMessage()),
						progressRepository);
				return new CsvImportResult("System error please contact support", 0);
			}
		} else {

			return new CsvImportResult("VALIDATION_ERROR", 0);
		}

	}

	public List<GameSales> readCsvData(MultipartFile file, CsvImportProgress progress) throws Exception {
		long startTime = System.currentTimeMillis();
		System.out.println("StartTime: " + startTime);

		List<GameSales> gameSalesList = new ArrayList<>();
		List<String> validationErrors = new ArrayList<>();
		boolean hasValidationErrors = false;

		try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
				CSVReader csvReader = new CSVReader(reader)) {
			String[] nextLine;
			csvReader.readNext();

			while ((nextLine = csvReader.readNext()) != null) {

				GameSales sale = new GameSales(nextLine[0], // id
						nextLine[1], // gameNo
						nextLine[2], // gameName
						nextLine[3], // gameCode
						nextLine[4], // type
						nextLine[5], // costPrice
						nextLine[6], // tax
						nextLine[7], // salePrice
						nextLine[8], // dateOfSale
						progress.getId() // csvId from progress
				);

				List<String> errors = GameSalesValidator.validate(sale);

				if (!errors.isEmpty()) {
					hasValidationErrors = true; // Flag that an error has occurred
					validationErrors.add("Row " + nextLine[0] + ": " + String.join(", ", errors));
				} else if (!hasValidationErrors) {
					gameSalesList.add(sale);
				}

			}

		} catch (IOException e) {
			ProgressUpdater.updateProgress(progress, "FILE_ERROR", 0, List.of("File error: " + e.getMessage()),
					progressRepository);

		}

		if (hasValidationErrors) {
			gameSalesList.clear();
			ProgressUpdater.updateProgress(progress, "VALIDATION_ERROR", 0, validationErrors, progressRepository);
		}

		long endTime = System.currentTimeMillis();
		System.out.println("endTime: " + endTime);
		System.out.println("CSV Validation: " + (endTime - startTime) / 1000 + " seconds");

		return gameSalesList;

	}

	private void batchInsertGameSales(List<GameSales> gameSalesList) {
		long startTime = System.currentTimeMillis();
		System.out.println("StartTime: " + startTime);

		jdbcTemplate.batchUpdate(sql, gameSalesList, 1000000, (ps, record) -> {
			ps.setString(1, record.getId());
			ps.setString(2, record.getGameNo());
			ps.setString(3, record.getGameName());
			ps.setString(4, record.getGameCode());
			ps.setString(5, record.getType());
			ps.setString(6, record.getCostPrice());
			ps.setString(7, record.getTax());
			ps.setString(8, record.getSalePrice());
			ps.setString(9, record.getDateOfSale());
			ps.setInt(10, record.getCsvId());
		});
		
		long endTime = System.currentTimeMillis();
		System.out.println("endTime: " + endTime);
		System.out.println("Batch Import ended total time taken: " + (endTime - startTime) / 1000 + "seconds");
		
		long startTime2 = System.currentTimeMillis();
		System.out.println("StartTime 2: " + startTime2);
		
		salesSummaryService.aggregateAndSaveGameSales(gameSalesList);
		
		long endTime2 = System.currentTimeMillis();
		System.out.println("endTime 2: " + endTime2);
		System.out.println("Aggregation: " + (startTime2 - endTime2) + "milliseconds");

	}

	public CsvImportProgress saveInitialProgress(MultipartFile file) throws IOException {
		CsvImportProgress progress = new CsvImportProgress();
		progress.setFileName(file.getOriginalFilename());
		progress.setFileData(file.getBytes());
		progress.setStartTime(LocalDateTime.now());
		progress.setStatus("PROCESSING");
		return progressRepository.save(progress);
	}

	public void clearTmpTable() {
		String sql = "TRUNCATE TABLE game_sales_db.game_sales";
		try (Connection connection = dataSource.getConnection();
				PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.execute();
			System.out.println("Temporary table cleared using TRUNCATE.");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// need to handle this
//	private void handleInsertionError(CsvImportProgress progress, DataAccessException e) throws Exception {
//	    ProgressUpdater.updateProgress(progress, "ERROR", 0, List.of("Failed to import: " + e.getMessage()), progressRepository);
//	    throw new Exception("Validation failed. Errors logged in CsvImportProgress.");
//	}

//	public String callValidationStoreProc(int fileId) {
//		String status = "";
//
//		try (Connection connection = dataSource.getConnection();
//				CallableStatement callableStatement = connection
//						.prepareCall("{call game_sales_db.importCsvValidation(?,?)}");) {
//
//			callableStatement.setInt(1, fileId);
//			callableStatement.registerOutParameter(2, Types.VARCHAR);
//			callableStatement.execute();
//
//			status = callableStatement.getString(2);
//			System.out.println("status: " + status);
//
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//
//		return status;
//
//	}
//
}
