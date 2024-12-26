package com.game_sale_import.Model;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

@Entity
@Table(name = "game_sales")
@IdClass(GameSalesId.class)
public class GameSales implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id", nullable = false)
	private String id;

	@Id
	@Column(name = "csv_id", nullable = false)
	private Integer csvId;

	@Column(name = "game_no")
	private String gameNo;

	@Column(name = "game_name")
	private String gameName;

	@Column(name = "game_code")
	private String gameCode;

	@Column(name = "type")
	private String type;

	@Column(name = "cost_price")
	private String costPrice;

	@Column(name = "tax")
	private String tax;

	@Column(name = "sale_price")
	private String salePrice;

	@Column(name = "date_of_sale")
	private String dateOfSale;

	public GameSales() {

	}

	public GameSales(String id, String gameNo, String gameName, String gameCode, String type, String costPrice,
			String tax, String salePrice, String dateOfSale, Integer csvId) {
		this.id = id;
		this.gameNo = gameNo;
		this.gameName = gameName;
		this.gameCode = gameCode;
		this.type = type;
		this.costPrice = costPrice;
		this.tax = tax;
		this.salePrice = salePrice;
		this.dateOfSale = dateOfSale;
		this.csvId = csvId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public Integer getCsvId() {
		return csvId;
	}

	public void setCsvId(Integer csvId) {
		this.csvId = csvId;
	}

}
