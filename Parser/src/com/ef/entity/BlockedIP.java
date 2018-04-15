package com.ef.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class BlockedIP {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private String ip;

	private String comments;

	public BlockedIP(String ip, String comments) {
		super();
		this.ip = ip;
		this.comments = comments;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public Long getId() {
		return id;
	}

	public BlockedIP() {

	}
}
