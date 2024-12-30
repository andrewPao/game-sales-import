package com.game_sale_import.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
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
import com.game_sale_import.Model.GameSalesValidateObj;
import com.game_sale_import.Repository.CsvImportProgressRepository;
import com.game_sale_import.Updater.ProgressUpdater;
import com.game_sale_import.Utils.FormatDate;
import com.game_sale_import.Validator.GameSalesValidator;
import com.opencsv.CSVReader;

@Service
public class CsvImportService {

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

				batchInsertGameSales(progress, gameSalesList);
				ProgressUpdater.updateProgress(progress, "COMPLETED", gameSalesList.size(), null, progressRepository);

				// Aggregation
				long startTime2 = System.currentTimeMillis();
				System.out.println("StartTime 2: " + startTime2);

				salesSummaryService.aggregateAndSaveGameSales(gameSalesList);

				long endTime2 = System.currentTimeMillis();
				System.out.println("endTime 2: " + endTime2);
				System.out.println("Aggregation: " + (endTime2 - startTime2) + "milliseconds");

			} catch (DataAccessException e) {
				ProgressUpdater.updateProgress(progress, "SYSTEM_ERROR", 0, List.of("System error: " + e.getMessage()),
						progressRepository);
			}

			if (progress.getStatus().equals("COMPLETED")) {
				return new CsvImportResult("COMPLETED", gameSalesList.size());
			} else {
				return new CsvImportResult("APPLICATION UPDATE IN PROGRESS PLEASE TRY AGAIN LATER", 0);
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
		HashSet<Integer> keys = new HashSet<>();
		boolean hasValidationErrors = false;

		try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
				CSVReader csvReader = new CSVReader(reader)) {
			String[] nextLine;
			csvReader.readNext();

			while ((nextLine = csvReader.readNext()) != null) {

				GameSalesValidateObj validateObj = new GameSalesValidateObj(nextLine[0], 
						nextLine[1], 
						nextLine[2], 
						nextLine[3], 
						nextLine[4],
						nextLine[5],
						nextLine[6],
						nextLine[7],
						nextLine[8],
						progress.getId()
				);

				List<String> errors = GameSalesValidator.validate(validateObj, keys);

				if (!errors.isEmpty()) {
					hasValidationErrors = true;
					validationErrors.add("Row " + nextLine[0] + ": " + String.join(", ", errors));

				} else if (!hasValidationErrors) {
					GameSales sale = new GameSales(Integer.parseInt(validateObj.getId()),
							Integer.parseInt(validateObj.getGameNo()), validateObj.getGameName(),
							validateObj.getGameCode(), Integer.parseInt(validateObj.getType()),
							Double.parseDouble(validateObj.getCostPrice()), Integer.parseInt(validateObj.getTax()),
							Double.parseDouble(validateObj.getSalePrice()),
							FormatDate.localDateFormatDDMMYYYY(validateObj.getDateOfSale()), validateObj.getCsvId());

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

	public void batchInsertGameSales(CsvImportProgress progress, List<GameSales> gameSalesList) {
		// Batch Import Process
		long startTime = System.currentTimeMillis();
		System.out.println("StartTime: " + startTime);

		jdbcTemplate.batchUpdate(sql, gameSalesList, 1000000, (ps, record) -> {
			ps.setInt(1, record.getId());
			ps.setInt(2, record.getGameNo());
			ps.setString(3, record.getGameName());
			ps.setString(4, record.getGameCode());
			ps.setInt(5, record.getType());
			ps.setDouble(6, record.getCostPrice());
			ps.setInt(7, record.getTax());
			ps.setDouble(8, record.getSalePrice());
			ps.setObject(9, record.getDateOfSale());
			ps.setInt(10, record.getCsvId());
		});

		long endTime = System.currentTimeMillis();
		System.out.println("endTime: " + endTime);
		System.out.println("Batch Import ended total time taken: " + (endTime - startTime) / 1000 + "seconds");

	}

	public CsvImportProgress saveInitialProgress(MultipartFile file) throws IOException {
		CsvImportProgress progress = new CsvImportProgress();
		progress.setFileName(file.getOriginalFilename());
		progress.setFileData(file.getBytes());
		progress.setStartTime(LocalDateTime.now());
		progress.setStatus("PROCESSING");
		return progressRepository.save(progress);
	}

}
