package com.game_sale_import.TestUtils;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import com.game_sale_import.Utils.FormatDate;

public class TestFormatDate {

	@Test
	public void testStringDateFormatDDMMYYYY() {

		String inputDate = "2024-12-31";
		String expectedFormattedDate = "31/12/2024";
		String result = FormatDate.stringDateFormatDDMMYYYY(inputDate);

		assertEquals(expectedFormattedDate, result);
	}

	@Test
	public void testLocalDateFormatDDMMYYYY() {

		String inputDate = "31/12/2024";
		LocalDate expectedDate = LocalDate.of(2024, 12, 31);
		LocalDate result = FormatDate.localDateFormatDDMMYYYY(inputDate);

		assertEquals(expectedDate, result);
	}

	@Test
	public void testLocalDateFormatDDMMYYYY_InvalidDate() {

		String invalidDate = "31-12-2024";

		assertThrows(RuntimeException.class, () -> {
			FormatDate.localDateFormatDDMMYYYY(invalidDate);
		});
	}

}
