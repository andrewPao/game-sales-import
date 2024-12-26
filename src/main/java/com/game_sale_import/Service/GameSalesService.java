package com.game_sale_import.Service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.game_sale_import.Model.GameSales;
import com.game_sale_import.Repository.GameSalesRepository;
import com.game_sale_import.Utils.FormatDate;

@Service
public class GameSalesService {
	
	@Autowired
	private GameSalesRepository gameSalesRepository;
	
	public Page<GameSales> getGameSales(Pageable pageable) {
		return gameSalesRepository.findAll(pageable);
	}

	public Page<GameSales> getGameSalesByDateRange(String fromDate, String toDate, Pageable pageable) {

		String fromDateString = FormatDate.stringDateFormatDDMMYYYY(fromDate);
		String toDateString = FormatDate.stringDateFormatDDMMYYYY(toDate);
		
		return gameSalesRepository.findByDateOfSaleBetween(fromDateString, toDateString, pageable);
	}

	public Page<GameSales> getGameSalesByPriceCondition(BigDecimal price, boolean lessThan, Pageable pageable) {
		return lessThan ? gameSalesRepository.findBySalePriceLessThan(price, pageable)
				: gameSalesRepository.findBySalePriceGreaterThan(price, pageable);
	}
	


}
