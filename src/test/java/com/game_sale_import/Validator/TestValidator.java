package com.game_sale_import.Validator;

import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import com.game_sale_import.Model.GameSalesValidateObj;

public class TestValidator {

	@Test
	public void testValidate_ValidData() {

		GameSalesValidateObj validObj = new GameSalesValidateObj("1", "10", "Test Game", "ABC12", "1", "50.0", "5",
				"60.0", "15/12/2024", 1);
		HashSet<Integer> keys = new HashSet<>();

		List<String> errors = GameSalesValidator.validate(validObj, keys);

		assertTrue(errors.isEmpty(), "Expected no validation errors for valid data.");
	}

	@Test
	public void testValidate_InvalidData() {

		GameSalesValidateObj invalidObj = new GameSalesValidateObj("abc", "200",
				"GameNameExceedLimitOf20", "ABCDEFG", "3", "200.0", "abc", "invalidSalePrice", "15-12-2024", 1);
		HashSet<Integer> keys = new HashSet<>();

		List<String> errors = GameSalesValidator.validate(invalidObj, keys);

		assertTrue(errors.contains("Invalid ID format."));
		assertTrue(errors.contains("Game Number must be between 1 and 100."));
		assertTrue(errors.contains("Game Name exceeded 20 characters."));
		assertTrue(errors.contains("Game Code exceeded 5 characters."));
		assertTrue(errors.contains("Invalid Game Type."));
		assertTrue(errors.contains("Invalid or too high Cost Price."));
		assertTrue(errors.contains("Invalid Tax format."));
		assertTrue(errors.contains("Invalid Sale Price format."));
		assertTrue(errors.contains("Invalid Date format. Expected DD/MM/YYYY."));
		assertEquals(9, errors.size(), "Expected 9 validation errors.");
	}

	@Test
	public void testValidate_DuplicateId() {

		GameSalesValidateObj obj1 = new GameSalesValidateObj("1", "10", "Test Game", "ABC12", "1", "50.0", "5", "60.0",
				"15/12/2024", 1);
		GameSalesValidateObj obj2 = new GameSalesValidateObj("1", "11", "Test Game 2", "XYZ34", "2", "30.0", "2",
				"35.0", "16/12/2024", 1);
		HashSet<Integer> keys = new HashSet<>();

		List<String> errors1 = GameSalesValidator.validate(obj1, keys);
		List<String> errors2 = GameSalesValidator.validate(obj2, keys);

		assertTrue(errors1.isEmpty(), "Expected no validation errors for the first object.");
		assertTrue(errors2.contains("Duplicate Id"), "Expected a duplicate ID error for the second object.");
	}

}
