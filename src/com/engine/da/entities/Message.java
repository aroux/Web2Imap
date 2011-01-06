package com.engine.da.entities;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.IndexColumn;

@Entity
@Table(name = "MESSAGE")
public class Message {
	
	@Id
	@GeneratedValue(generator = "message_increment")
	@GenericGenerator(name="message_increment", strategy = "increment")
	@Column(name = "UID")
	private Integer uid;
	
	@IndexColumn(name = "WEBMAIL_UID")
	private Long webmailuid;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DATE")
	private Date date;
	
	@Column(name = "CONTENT", length = 10240000)
	private byte[] content;
	
	@Column(name = "FLAG")
	private Integer flag;
	
	public Message() {
	}

	public Message(Integer uid, Long webmailuid, byte[] message) {
		super();
		this.uid = uid;
		this.webmailuid = webmailuid;
		this.content = message;
	}

	public Long getWebmailuid() {
		return webmailuid;
	}

	public void setWebmailuid(Long webmailuid) {
		this.webmailuid = webmailuid;
	}

	public Integer getUid() {
		return uid;
	}

	public void setUid(Integer uid) {
		this.uid = uid;
	}

	public byte[] getContent() {
		return content;
	}

	public void setContent(byte[] message) {
		this.content = message;
	}
	
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
	
	public Integer getFlag() {
		return flag;
	}
	
	public void setFlag(Integer flag) {
		this.flag = flag;
	}
}
