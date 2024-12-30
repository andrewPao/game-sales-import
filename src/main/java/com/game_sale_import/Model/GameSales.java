package com.game_sale_import.Model;

import java.io.Serializable;
import java.time.LocalDate;

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
	private int id;

	@Column(name = "game_no")
	private int gameNo;

	@Column(name = "game_name", length = 20)
	private String gameName;

	@Column(name = "game_code", length = 5)
	private String gameCode;

	@Column(name = "type")
	private int type;

	@Column(name = "cost_price")
	private double costPrice;

	@Column(name = "tax")
	private int tax;

	@Column(name = "sale_price")
	private double salePrice;

	@Column(name = "date_of_sale")
	private LocalDate dateOfSale;

	@Id
	@Column(name = "csv_id")
	private int csvId;

	public GameSales() {

	}

	public GameSales(int id, int gameNo, String gameName, String gameCode, int type, double costPrice, int tax,
			double salePrice, LocalDate dateOfSale, int csvId) {

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

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getGameNo() {
		return gameNo;
	}

	public void setGameNo(int gameNo) {
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

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public double getCostPrice() {
		return costPrice;
	}

	public void setCostPrice(double costPrice) {
		this.costPrice = costPrice;
	}

	public int getTax() {
		return tax;
	}

	public void setTax(int tax) {
		this.tax = tax;
	}

	public double getSalePrice() {
		return salePrice;
	}

	public void setSalePrice(double salePrice) {
		this.salePrice = salePrice;
	}

	public LocalDate getDateOfSale() {
		return dateOfSale;
	}

	public void setDateOfSale(LocalDate dateOfSale) {
		this.dateOfSale = dateOfSale;
	}

	public int getCsvId() {
		return csvId;
	}

	public void setCsvId(int csvId) {
		this.csvId = csvId;
	}

}
