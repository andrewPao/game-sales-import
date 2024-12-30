package com.game_sale_import.Updater;

import java.time.LocalDateTime;
import java.util.List;

import com.game_sale_import.Model.CsvImportProgress;
import com.game_sale_import.Repository.CsvImportProgressRepository;

public class ProgressUpdater {

	public static void updateProgress(CsvImportProgress progress, String status, int totalRecords, List<String> errors,
			CsvImportProgressRepository repository) {
		progress.setStatus(status);
		progress.setEndTime(LocalDateTime.now());

		if ("COMPLETED".equalsIgnoreCase(status)) {
			progress.setTotalRecords(totalRecords);
		} else if ((status.equalsIgnoreCase("VALIDATION_ERROR") || status.equalsIgnoreCase("SYSTEM_ERROR"))
				&& errors != null) {
			progress.setErrorLog(String.join("\n", errors).getBytes());
		}

		repository.save(progress);
	}

}
