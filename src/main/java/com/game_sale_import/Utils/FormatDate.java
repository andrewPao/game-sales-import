package com.game_sale_import.Utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class FormatDate {
	
	public static String stringDateFormatDDMMYYYY(String date) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		date = LocalDate.parse(date).format(formatter);
		
		return date;
	}
	
	public static LocalDate localDateFormatDDMMYYYY(String date) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		LocalDate parsedDate = LocalDate.parse(date, formatter);
		
		return parsedDate;
	}

}
