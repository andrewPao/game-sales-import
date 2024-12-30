package com.game_sale_import.Validator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.game_sale_import.Model.GameSalesValidateObj;

public class GameSalesValidator {

	public static List<String> validate(GameSalesValidateObj validateObj, HashSet<Integer> keys) {

		List<String> errors = new ArrayList<>();

		if (validateObj.getId() == null || !validateObj.getId().matches("^[0-9]+$")) {
			errors.add("Invalid ID format.");
		}else{
			int id = Integer.parseInt(validateObj.getId());
			if (!keys.add(id)) {
				errors.add("Duplicate Id");
			}
		}
		
		if (validateObj.getGameNo() == null || !validateObj.getGameNo().matches("^[0-9]+$")) {
			errors.add("Invalid Game Number format.");
		} else {
			int gameNo = Integer.parseInt(validateObj.getGameNo());
			if (gameNo < 1 || gameNo > 100) {
				errors.add("Game Number must be between 1 and 100.");
			}
		}

		if (validateObj.getGameName() != null && validateObj.getGameName().length() > 20) {
			errors.add("Game Name exceeded 20 characters.");
		}

		if (validateObj.getGameCode() != null && validateObj.getGameCode().length() > 5) {
			errors.add("Game Code exceeded 5 characters.");
		}

		if (validateObj.getType() == null
				|| (!validateObj.getType().equals("1") && !validateObj.getType().equals("2"))) {
			errors.add("Invalid Game Type.");
		}

		if (validateObj.getCostPrice() == null || !validateObj.getCostPrice().matches("^[0-9]+(\\.[0-9]+)?$")
				|| new BigDecimal(validateObj.getCostPrice()).compareTo(BigDecimal.valueOf(100)) > 0) {
			errors.add("Invalid or too high Cost Price.");
		}

		if (validateObj.getTax() == null || !validateObj.getTax().matches("^[0-9]+$")) {
			errors.add("Invalid Tax format.");
		}

		if (validateObj.getSalePrice() == null || !validateObj.getSalePrice().matches("^[0-9]+(\\.[0-9]+)?$")) {
			errors.add("Invalid Sale Price format.");
		}

		try {
			LocalDate.parse(validateObj.getDateOfSale(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
		} catch (Exception e) {
			errors.add("Invalid Date format. Expected DD/MM/YYYY.");
		}

		return errors;
	}

}
