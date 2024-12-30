package com.game_sale_import.Model;

public class GameSalesValidateObj {
	
	private String id;

	private Integer csvId;

	private String gameNo;

	private String gameName;

	private String gameCode;

	private String type;

	private String costPrice;

	private String tax;

	private String salePrice;

	private String dateOfSale;
	
	public GameSalesValidateObj() {
		
	}
	
	public GameSalesValidateObj(String id,  String gameNo, String gameName, String gameCode, String type,
			String costPrice, String tax, String salePrice, String dateOfSale, Integer csvId) {
		
		this.id = id;
		this.csvId = csvId;
		this.gameNo = gameNo;
		this.gameName = gameName;
		this.gameCode = gameCode;
		this.type = type;
		this.costPrice = costPrice;
		this.tax = tax;
		this.salePrice = salePrice;
		this.dateOfSale = dateOfSale;
	}



	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Integer getCsvId() {
		return csvId;
	}

	public void setCsvId(Integer csvId) {
		this.csvId = csvId;
	}

	public String getGameNo() {
		return gameNo;
	}

	public void setGameNo(String gameNo) {
		this.gameNo = gameNo;
	}

	public String getGameName() {
		return gameName;
	}

	public void setGameName(String gameName) {
		this.gameName = gameName;
	}

	public String getGameCode() {
		return gameCode;
	}

	public void setGameCode(String gameCode) {
		this.gameCode = gameCode;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getCostPrice() {
		return costPrice;
	}

	public void setCostPrice(String costPrice) {
		this.costPrice = costPrice;
	}

	public String getTax() {
		return tax;
	}

	public void setTax(String tax) {
		this.tax = tax;
	}

	public String getSalePrice() {
		return salePrice;
	}

	public void setSalePrice(String salePrice) {
		this.salePrice = salePrice;
	}

	public String getDateOfSale() {
		return dateOfSale;
	}

	public void setDateOfSale(String dateOfSale) {
		this.dateOfSale = dateOfSale;
	}
	
}
