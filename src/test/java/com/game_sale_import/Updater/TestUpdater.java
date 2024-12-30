package com.game_sale_import.Updater;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.game_sale_import.Model.CsvImportProgress;
import com.game_sale_import.Repository.CsvImportProgressRepository;

public class TestUpdater {
	
    @Mock
    private CsvImportProgressRepository repository;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testUpdateProgress_Completed() {
        
        CsvImportProgress progress = new CsvImportProgress();
        progress.setStatus("PROCESSING");
        progress.setStartTime(LocalDateTime.now());

        ProgressUpdater.updateProgress(progress, "COMPLETED", 10, null, repository);

        assert "COMPLETED".equals(progress.getStatus());
        assert progress.getEndTime() != null;
        assert progress.getTotalRecords() == 10;
        verify(repository, times(1)).save(progress);
    }

    @Test
    public void testUpdateProgress_ValidationError() {
       
        CsvImportProgress progress = new CsvImportProgress();
        progress.setStatus("PROCESSING");
        progress.setStartTime(LocalDateTime.now());
        List<String> errors = List.of("Error 1", "Error 2");

        ProgressUpdater.updateProgress(progress, "VALIDATION_ERROR", 0, errors, repository);

        assert "VALIDATION_ERROR".equals(progress.getStatus());
        assert progress.getEndTime() != null;
        assert new String(progress.getErrorLog()).equals("Error 1\nError 2");
        verify(repository, times(1)).save(progress);
    }

    @Test
    public void testUpdateProgress_SystemError() {
       
        CsvImportProgress progress = new CsvImportProgress();
        progress.setStatus("PROCESSING");
        progress.setStartTime(LocalDateTime.now());
        List<String> errors = List.of("System crash", "Database connection failure");

        ProgressUpdater.updateProgress(progress, "SYSTEM_ERROR", 0, errors, repository);

        assert "SYSTEM_ERROR".equals(progress.getStatus());
        assert progress.getEndTime() != null;
        assert new String(progress.getErrorLog()).equals("System crash\nDatabase connection failure");
        verify(repository, times(1)).save(progress);
    }

}
