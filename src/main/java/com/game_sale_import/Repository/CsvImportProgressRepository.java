package com.game_sale_import.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.game_sale_import.Model.CsvImportProgress;

@Repository
public interface CsvImportProgressRepository extends JpaRepository<CsvImportProgress, Integer>{

}
