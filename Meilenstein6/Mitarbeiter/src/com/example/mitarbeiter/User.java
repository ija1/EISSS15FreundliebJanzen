package com.example.mitarbeiter;



/**
 * Class User for creating Profile and Chatting
 * 
 * @author Jan Freundlieb, Irene Janzen (System ABC)
 * @version 2015.01
 */
public class User {
	
	private String vorName;
	private String nachName;
	private int alter;
	private String email;
	private String betrieb;
	private String rolle;
	private String jobcoachVorname;
	private String jobcoachNachname;
	private String Position;
	
	public String getJobcoachVorname() {
		return jobcoachVorname;
	}
	public void setJobcoachVorname(String jobcoachVorname) {
		this.jobcoachVorname = jobcoachVorname;
	}
	public String getJobcoachNachname() {
		return jobcoachNachname;
	}
	public void setJobcoachNachname(String jobcoachNachname) {
		this.jobcoachNachname = jobcoachNachname;
	}
	public String getVorName() {
		return vorName;
	}
	public void setVorName(String vorName) {
		this.vorName = vorName;
	}
	public String getNachName() {
		return nachName;
	}
	public void setNachName(String nachName) {
		this.nachName = nachName;
	}
	public int getAlter() {
		return alter;
	}
	public void setAlter(int alter) {
		this.alter = alter;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getBetrieb() {
		return betrieb;
	}
	public void setBetrieb(String betrieb) {
		this.betrieb = betrieb;
	}
	public String getPosition() {
		return Position;
	}
	public void setPosition(String position) {
		Position = position;
	}
	public String getRolle() {
		return rolle;
	}
	public void setRolle(String rolle) {
		this.rolle = rolle;
	}
}
