package com.game_sale_import.Model;

public class CsvImportResult {
	
	private String status;
    private int totalRecords;

    public CsvImportResult(String status, int totalRecords) {
        this.status = status;
        this.totalRecords = totalRecords;

    }

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public int getTotalRecords() {
		return totalRecords;
	}

	public void setTotalRecords(int totalRecords) {
		this.totalRecords = totalRecords;
	}

}
