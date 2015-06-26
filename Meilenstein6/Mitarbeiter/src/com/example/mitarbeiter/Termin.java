package com.example.mitarbeiter;



/**
 * Class Termin for the Termin arangement between jobcoach and autist
 * @author Jan Freundlieb, Irene Janzen (System ABC)
 * @version 2015.01
 */
public class Termin {
	private String betreff;
	
	private long startDatum;
	private long endDatum;
	private String bemerkung;
	private boolean check;
	
	// Variables for the Use Case Termin overlap
	private String betreffOverlap;
	private long startDatumOverlap;
	private long endDatumOverlap;
	private String bemerkungOverlap;
	
	User autist;
	
	public User getAutist() {
		return autist;
	}
	public void setAutist(User autist) {
		this.autist = autist;
	}
	public String getBetreffOverlap() {
		return betreffOverlap;
	}
	public void setBetreffOverlap(String betreffOverlap) {
		this.betreffOverlap = betreffOverlap;
	}
	public long getStartDatumOverlap() {
		return startDatumOverlap;
	}
	public void setStartDatumOverlap(long startDatumOverlap) {
		this.startDatumOverlap = startDatumOverlap;
	}
	public long getEndDatumOverlap() {
		return endDatumOverlap;
	}
	public void setEndDatumOverlap(long endDatumOverlap) {
		this.endDatumOverlap = endDatumOverlap;
	}
	public String getBemerkungOverlap() {
		return bemerkungOverlap;
	}
	public void setBemerkungOverlap(String bemerkungOverlap) {
		this.bemerkungOverlap = bemerkungOverlap;
	}
	public String getBetreff() {
		return betreff;
	}
	public void setBetreff(String betreff) {
		this.betreff = betreff;
	}
	public long getStartDatum() {
		return startDatum;
	}
	public void setStartDatum(long startDatum) {
		this.startDatum = startDatum;
	}
	public long getEndDatum() {
		return endDatum;
	}
	public void setEndDatum(long endDatum) {
		this.endDatum = endDatum;
	}
	public String getBemerkung() {
		return bemerkung;
	}
	public void setBemerkung(String bemerkung) {
		this.bemerkung = bemerkung;
	}
	public boolean isCheck() {
		return check;
	}
	public void setCheck(boolean check) {
		this.check = check;
	}
}
