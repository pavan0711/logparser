package com.ef.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class AccessLog {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "startTime", columnDefinition = "DATETIME")
	@Temporal(TemporalType.TIMESTAMP)
	private Date date;

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getRequest() {
		return request;
	}

	public void setRequest(String request) {
		this.request = request;
	}

	public String getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}

	public String getDeviceMetadata() {
		return deviceMetadata;
	}

	public void setDeviceMetadata(String deviceMetadata) {
		this.deviceMetadata = deviceMetadata;
	}

	public AccessLog(Date date, String ip, String request, String responseCode, String deviceMetadata) {
		super();
		this.date = date;
		this.ip = ip;
		this.request = request;
		this.responseCode = responseCode;
		this.deviceMetadata = deviceMetadata;
	}

	public Long getId() {
		return id;
	}

	private String ip;

	private String request;

	private String responseCode;

	private String deviceMetadata;

	public AccessLog() {

	}
}
