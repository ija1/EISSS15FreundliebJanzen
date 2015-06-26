package com.notfallchat.autist;



import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Class for the Action would be set by the Jobcoach
 * 
 * @author Jan Freundlieb, Irene Janzen (System ABC)
 * @version 2015.01
 */
public class Handlungsoption implements Serializable{
	
	//private List<Situation> situationen;
	private String title;
	private List<String> schritte;
	private String reason;
	private List<String> ChatAboutAction;
	
	private String commentRatingFromAutist;
	private String commentRatingRatingFromEmployee;
	
	/**
	 * Booleans that are very importing for the statistics
	 */
	private boolean goodRatingFromAutist;
	private boolean badRatingFromAutist;
	private boolean centralRatingFromAutist;
	private boolean goodRatingFromEmployee;
	private boolean badRatingFromEmployee;
	private boolean centralRatingFromEmployee;
	
	private UUID actionId;
	private User jobcoach;
	
	/**
	 * Constructor of Class Handlungsoption
	 */
	public Handlungsoption() {
		this.schritte = null;
		this.ChatAboutAction = null;
		this.goodRatingFromAutist = false;
		this.badRatingFromAutist = false;
		this.centralRatingFromAutist = false;
		this.goodRatingFromEmployee = false;
		this.badRatingFromEmployee = false;
		this.centralRatingFromEmployee = false;
		this.jobcoach = null;
	}
	
	
	public UUID getActionId() {
		return actionId;
	}

	public void setActionId(UUID actionId) {
		this.actionId = actionId;
	}
	
	public User getJobcoach() {
		return jobcoach;
	}

	public void setJobcoach(User jobcoach) {
		this.jobcoach = jobcoach;
	}

	public String getCommentRatingFromAutist() {
		return commentRatingFromAutist;
	}

	public void setCommentRatingFromAutist(String commentRatingFromAutist) {
		this.commentRatingFromAutist = commentRatingFromAutist;
	}

	public boolean isGoodRatingFromAutist() {
		return goodRatingFromAutist;
	}

	public void setGoodRatingFromAutist(boolean goodRatingFromAutist) {
		this.goodRatingFromAutist = goodRatingFromAutist;
	}

	public boolean isBadRatingFromAutist() {
		return badRatingFromAutist;
	}

	public void setBadRatingFromAutist(boolean badRatingFromAutist) {
		this.badRatingFromAutist = badRatingFromAutist;
	}

	public boolean isCentralRatingFromAutist() {
		return centralRatingFromAutist;
	}

	public void setCentralRatingFromAutist(boolean centralRatingFromAutist) {
		this.centralRatingFromAutist = centralRatingFromAutist;
	}

	public String getCommentRatingRatingFromEmployee() {
		return commentRatingRatingFromEmployee;
	}

	public void setCommentRatingRatingFromEmployee(
			String commentRatingRatingFromEmployee) {
		this.commentRatingRatingFromEmployee = commentRatingRatingFromEmployee;
	}

	public boolean isGoodRatingFromEmployee() {
		return goodRatingFromEmployee;
	}

	public void setGoodRatingFromEmployee(boolean goodRatingFromEmployee) {
		this.goodRatingFromEmployee = goodRatingFromEmployee;
	}

	public boolean isBadRatingFromEmployee() {
		return badRatingFromEmployee;
	}

	public void setBadRatingFromEmployee(boolean badRatingFromEmployee) {
		this.badRatingFromEmployee = badRatingFromEmployee;
	}

	public boolean isCentralRatingFromEmployee() {
		return centralRatingFromEmployee;
	}

	public void setCentralRatingFromEmployee(boolean centralRatingFromEmployee) {
		this.centralRatingFromEmployee = centralRatingFromEmployee;
	}

	public List<String> getChatAboutAction() {
		return ChatAboutAction;
	}

	public void setChatAboutAction(String chatAboutAction) {
		if (ChatAboutAction == null) {
			this.ChatAboutAction = new ArrayList<String>();
		}
		this.ChatAboutAction.add(chatAboutAction);
	}

	public List<String> getSchritte() {
		return schritte;
	}

	public void setSchritte(int i, String schritt) {
		if (schritte == null) {
			this.schritte = new ArrayList<String>();
		}
		this.schritte.add(i, schritt);
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
}
