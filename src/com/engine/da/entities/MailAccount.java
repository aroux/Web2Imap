package com.engine.da.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "ACCOUNT")
public class MailAccount {

	@Id
	@GeneratedValue(generator = "account_increment")
	@GenericGenerator(name="account_increment", strategy = "increment")
	@Column(name = "id")
	private Integer id;
	
	@Column(name = "EMAIL_ADDRESS")
	private String emailAddress;
	
	@Column(name = "LAST_WEBMAILUID")
	private Long lastWebmailuid;

	
	public MailAccount(String emailAddress) {
		super();
		this.emailAddress = emailAddress;
		this.lastWebmailuid = 0L;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public Long getLastWebmailuid() {
		return lastWebmailuid;
	}

	public void setLastWebmailuid(Long lastWebmailuid) {
		this.lastWebmailuid = lastWebmailuid;
	}
	
	
}
