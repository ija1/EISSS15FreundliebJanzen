package com.example.jobcoach;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for the Categories
 * 
 * @author Jan Freundlieb, Irene Janzen (System ABC)
 * @version 2015.01
 */
public class Kategorie {
	
	private String name;
	
	/**
	 * Every categorie can have a sub categorie
	 */
	private List<Kategorie> unterkategorie = null;
	
	public Kategorie() {
		this.unterkategorie = new ArrayList<Kategorie>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Kategorie getUnterkategorie(int i) {
		return this.getUnterkategorie(i);
	}

	public void setUnterkategorie(int i,Kategorie kategorie) {
		if (this.unterkategorie == null) {
			this.unterkategorie = new ArrayList<Kategorie>();
		}
		this.unterkategorie.add(i, kategorie);
	}

}
