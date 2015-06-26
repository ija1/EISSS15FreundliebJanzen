package com.example.jobcoach;

/**
 * Class StatisticTabel to calculate sum of all categorie ratings
 * 
 * @author Jan Freundlieb, Irene Janzen (System ABC)
 * @version 2015.01
 */
public class StatisticTabel {
	private int countGoodEmployee;
	private int countCentralEmployee;
	private int countBadEmployee;
	
	private int countGoodAutist;
	private int countCentralutist;
	private int countBadAutist;
	
	private String categorie;
	
	
	public StatisticTabel() {
		countGoodEmployee = 0;
		countCentralEmployee = 0;
		countBadEmployee = 0;
		
		countGoodAutist = 0;
		countCentralutist = 0;
		countBadAutist = 0;
	}

	public int getCountGoodEmployee() {
		return countGoodEmployee;
	}

	public void setCountGoodEmployee(int countGoodEmployee) {
		this.countGoodEmployee = countGoodEmployee;
	}

	public int getCountCentralEmployee() {
		return countCentralEmployee;
	}

	public void setCountCentralEmployee(int countCentralEmployee) {
		this.countCentralEmployee = countCentralEmployee;
	}

	public int getCountBadEmployee() {
		return countBadEmployee;
	}

	public void setCountBadEmployee(int countBadEmployee) {
		this.countBadEmployee = countBadEmployee;
	}

	public int getCountGoodAutist() {
		return countGoodAutist;
	}

	public void setCountGoodAutist(int countGoodAutist) {
		this.countGoodAutist = countGoodAutist;
	}

	public int getCountCentralutist() {
		return countCentralutist;
	}

	public void setCountCentralutist(int countCentralutist) {
		this.countCentralutist = countCentralutist;
	}

	public int getCountBadAutist() {
		return countBadAutist;
	}

	public void setCountBadAutist(int countBadAutist) {
		this.countBadAutist = countBadAutist;
	}

	public String getCategorie() {
		return categorie;
	}

	public void setCategorie(String categorie) {
		this.categorie = categorie;
	}

}
