package com.game_sale_import.Validator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.game_sale_import.Model.GameSales;

public class GameSalesValidator {

	public static List<String> validate(GameSales sale) {

		List<String> errors = new ArrayList<>();

		// Validate ID
		if (sale.getId() == null || !sale.getId().matches("^[0-9]+$")) {
			errors.add("Invalid ID format.");
		}

		// Validate Game Number
		if (sale.getGameNo() == null || !sale.getGameNo().matches("^[0-9]+$")) {
			errors.add("Invalid Game Number format.");
		} else {
			int gameNo = Integer.parseInt(sale.getGameNo());
			if (gameNo < 1 || gameNo > 100) {
				errors.add("Game Number must be between 1 and 100.");
			}
		}

		// Validate Game Name
		if (sale.getGameName() != null && sale.getGameName().length() > 20) {
			errors.add("Game Name exceeded 20 characters.");
		}

		// Validate Game Code
		if (sale.getGameCode() != null && sale.getGameCode().length() > 5) {
			errors.add("Game Code exceeded 5 characters.");
		}

		// Validate Type
		if (sale.getType() == null || (!sale.getType().equals("1") && !sale.getType().equals("2"))) {
			errors.add("Invalid Game Type.");
		}

		// Validate Cost Price
		if (sale.getCostPrice() == null || !sale.getCostPrice().matches("^[0-9]+(\\.[0-9]+)?$")
				|| new BigDecimal(sale.getCostPrice()).compareTo(BigDecimal.valueOf(100)) > 0) {
			errors.add("Invalid or too high Cost Price.");
		}

		// Validate Tax
		if (sale.getTax() == null || !sale.getTax().matches("^[0-9]+$")) {
			errors.add("Invalid Tax format.");
		}

		// Validate Sale Price
		if (sale.getSalePrice() == null || !sale.getSalePrice().matches("^[0-9]+(\\.[0-9]+)?$")) {
			errors.add("Invalid Sale Price format.");
		}

		// Validate Date of Sale
		try {
			LocalDate.parse(sale.getDateOfSale(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
		} catch (Exception e) {
			errors.add("Invalid Date format. Expected DD/MM/YYYY.");
		}

		return errors;
	}

}
