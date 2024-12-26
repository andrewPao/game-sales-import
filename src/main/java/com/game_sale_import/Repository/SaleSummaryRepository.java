package com.game_sale_import.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.game_sale_import.Model.SaleSummary;

public interface SaleSummaryRepository extends JpaRepository<SaleSummary, Long > {

}
