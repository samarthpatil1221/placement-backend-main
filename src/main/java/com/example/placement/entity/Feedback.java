package com.example.placement.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.GenerationType;

@Entity
@Table(name = "FEEDBACK") // Oracle resolves unquoted to uppercase table FEEDBACK
@SequenceGenerator(name = "FEEDBACK_SEQ", sequenceName = "FEEDBACK_SEQ", allocationSize = 1)
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "FEEDBACK_SEQ")
    private Long id;

    private String company;
    private String category;
    private String subject;
    private String details;
    private String suggestions;
    private boolean anonymous;

    // Use wrapper to allow null when category != 'interview'
    private Integer interviewRounds;
    private String interviewDescription;
    private String roundReached;
    private String interviewDifficulty;
    private String interviewDate;
    private String interviewOutcome;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getCompany() {
		return company;
	}
	public void setCompany(String company) {
		this.company = company;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getDetails() {
		return details;
	}
	public void setDetails(String details) {
		this.details = details;
	}
	public String getSuggestions() {
		return suggestions;
	}
	public void setSuggestions(String suggestions) {
		this.suggestions = suggestions;
	}
	public boolean isAnonymous() {
		return anonymous;
	}
	public void setAnonymous(boolean anonymous) {
		this.anonymous = anonymous;
	}
	public Integer getInterviewRounds() { return interviewRounds; }
	public void setInterviewRounds(int interviewRounds) {
		this.interviewRounds = interviewRounds;
	}
	public String getInterviewDescription() {
		return interviewDescription;
	}
	public void setInterviewRounds(Integer interviewRounds) { this.interviewRounds = interviewRounds; }

	public String getRoundReached() {
		return roundReached;
	}
	public void setRoundReached(String roundReached) {
		this.roundReached = roundReached;
	}
	public String getInterviewDifficulty() {
		return interviewDifficulty;
	}
	public void setInterviewDifficulty(String interviewDifficulty) {
		this.interviewDifficulty = interviewDifficulty;
	}
	public String getInterviewDate() {
		return interviewDate;
	}
	public void setInterviewDate(String interviewDate) {
		this.interviewDate = interviewDate;
	}
	public String getInterviewOutcome() {
		return interviewOutcome;
	}
	public void setInterviewOutcome(String interviewOutcome) {
		this.interviewOutcome = interviewOutcome;
	}

    // Getters and setters...
    
}
