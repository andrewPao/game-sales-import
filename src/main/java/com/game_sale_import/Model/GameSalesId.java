package com.game_sale_import.Model;

import java.io.Serializable;
import java.util.Objects;

public class GameSalesId implements Serializable {
	
	private String id;       
    private int csvId;

    public GameSalesId() {}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getCsvId() {
		return csvId;
	}

	public void setCsvId(int csvId) {
		this.csvId = csvId;
	}

	@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameSalesId that = (GameSalesId) o;
        return Objects.equals(id, that.id) && Objects.equals(csvId, that.csvId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, csvId);
    }

}
