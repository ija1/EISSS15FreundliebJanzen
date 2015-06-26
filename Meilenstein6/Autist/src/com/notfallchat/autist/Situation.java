package com.notfallchat.autist;



import java.util.ArrayList;
import java.util.List;

/**
 * Class Situation for Situations in which autist doesn't know what to do
 * 
 * @author Jan Freundlieb, Irene Janzen (System ABC)
 * @version 2015.01
 */
public class Situation {
	
	private User autist;
	private Kategorie kategorie;
	private List<User> mitarbeiterImKontext;
	private String Situationsbeschreibung;
	private List<Handlungsoption> handlungsoptionenen = null;
	private Handlungsoption notfallhandlung = null;
	/**
	 * from is important for two reasons: for knowing who is chatting with each other, and for employee to know which autist to evaluate
	 */
	private String from;
	
	private String askForSituation;
	
	public String getAskForSituation() {
		return askForSituation;
	}
	public void setAskForSituation(String askForSituation) {
		this.askForSituation = askForSituation;
	}
	public Handlungsoption getNotfallhandlung() {
		return notfallhandlung;
	}
	public void setNotfallhandlung(Handlungsoption notfallhandlung) {
		this.notfallhandlung = notfallhandlung;
	}

	
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public User getAutist() {
		return autist;
	}
	public void setAutist(User autist) {
		this.autist = autist;
	}
	public Kategorie getKategorie() {
		return kategorie;
	}
	public void setKategorie(Kategorie kategorie) {
		this.kategorie = kategorie;
	}
	public User getMitarbeiterImKontext(int i) {
		return mitarbeiterImKontext.get(i);
	}
	public void setMitarbeiterImKontext(int i, User mitarbeiterImKontext) {
		if (this.mitarbeiterImKontext == null) {
			this.mitarbeiterImKontext = new ArrayList<User>();
		}
		this.mitarbeiterImKontext.add(i, mitarbeiterImKontext);
	}
	public String getSituationsbeschreibung() {
		return Situationsbeschreibung;
	}
	public void setSituationsbeschreibung(String situationsbeschreibung) {
		Situationsbeschreibung = situationsbeschreibung;
	}
	public List<Handlungsoption> getHandlungsoptionenen() {
		if (this.handlungsoptionenen != null) {
			return handlungsoptionenen;
		}
		return null;
	}
	public void setHandlungsoptionenen(int i, Handlungsoption handlungsoptionen) {
		if (this.handlungsoptionenen == null) {
			this.handlungsoptionenen = new ArrayList<Handlungsoption>();
		}
		this.handlungsoptionenen.add(i, handlungsoptionen);
	}
}
