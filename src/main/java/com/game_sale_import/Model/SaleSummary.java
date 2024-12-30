package com.game_sale_import.Model;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "sale_summary", schema = "game_sales_db",
uniqueConstraints = @UniqueConstraint(columnNames = {"game_no", "date_of_sale"}))
public class SaleSummary {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@Column(name = "game_no", nullable = false)
	private Integer gameNo;
	
	@Column(name = "game_name", nullable = false)
	private String gameName;

	@Column(name = "date_of_sale", nullable = false)
	private LocalDate dateOfSale;

	@Column(name = "total_no_of_game_sold", nullable = false)
	private Integer totalNoOfGameSold;

	@Column(name = "sale_price", nullable = false, precision = 10, scale = 2)
	private BigDecimal sale_price;

	public SaleSummary() {

	}
	
	public SaleSummary(int gameNo, LocalDate dateOfSale) {
		this.gameNo = gameNo;
		this.dateOfSale = dateOfSale;
	}

	public SaleSummary(Long id, Integer gameNo, String gameName,  LocalDate dateOfSale, Integer totalNoOfGameSold,
			BigDecimal sale_price) {
		this.id = id;
		this.gameNo = gameNo;
		this.gameName = gameName;
		this.dateOfSale = dateOfSale;
		this.totalNoOfGameSold = totalNoOfGameSold;
		this.sale_price = sale_price;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getGameNo() {
		return gameNo;
	}

	public void setGameNo(Integer gameNo) {
		this.gameNo = gameNo;
	}

	public String getGameName() {
		return gameName;
	}

	public void setGameName(String gameName) {
		this.gameName = gameName;
	}

	public LocalDate getDateOfSale() {
		return dateOfSale;
	}

	public void setDateOfSale(LocalDate dateOfSale) {
		this.dateOfSale = dateOfSale;
	}

	public Integer getTotalNoOfGameSold() {
		return totalNoOfGameSold;
	}

	public void setTotalNoOfGameSold(Integer totalNoOfGameSold) {
		this.totalNoOfGameSold = totalNoOfGameSold;
	}

	public BigDecimal getSale_price() {
		return sale_price;
	}

	public void setSale_price(BigDecimal sale_price) {
		this.sale_price = sale_price;
	}

}
