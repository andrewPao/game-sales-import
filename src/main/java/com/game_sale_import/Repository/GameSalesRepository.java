package com.game_sale_import.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.game_sale_import.Model.GameSales;
import com.game_sale_import.Model.GameSalesId;

public interface GameSalesRepository extends JpaRepository<GameSales,GameSalesId>{

}
