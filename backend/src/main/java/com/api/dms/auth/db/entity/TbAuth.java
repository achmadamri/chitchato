package com.api.dms.auth.db.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;


/**
 * The persistent class for the tb_users database table.
 * 
 */
@Entity
@Table(name="tb_auth")
@NamedQuery(name="TbAuth.findAll", query="SELECT t FROM TbAuth t")
public class TbAuth implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="tba_id")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer tbaTid;

	@Column(name="tba_create_date")
	private Date tbaCreateDate;

	@Column(name="tba_create_id")
	private Integer tbaCreateId;

	@Column(name="tba_update_date")
	private Date tbaUpdateDate;

	@Column(name="tba_update_id")
	private Integer tbaUpdateId;

	@Column(name="tba_email")
	private String tbaEmail;

	@Column(name="tba_password")
	private String tbaPassword;

	@Column(name="tba_status")
	private String tbaStatus;

	@Column(name="tba_token_salt")
	private String tbaTokenSalt;

	@Column(name="tba_role")
	private String tbaRole;

	public Integer getTbaTid() {
		return tbaTid;
	}

	public void setTbaTid(Integer tbaTid) {
		this.tbaTid = tbaTid;
	}

	public Date getTbaCreateDate() {
		return tbaCreateDate;
	}

	public void setTbaCreateDate(Date tbaCreateDate) {
		this.tbaCreateDate = tbaCreateDate;
	}

	public Integer getTbaCreateId() {
		return tbaCreateId;
	}

	public void setTbaCreateId(Integer tbaCreateId) {
		this.tbaCreateId = tbaCreateId;
	}

	public Date getTbaUpdateDate() {
		return tbaUpdateDate;
	}

	public void setTbaUpdateDate(Date tbaUpdateDate) {
		this.tbaUpdateDate = tbaUpdateDate;
	}

	public Integer getTbaUpdateId() {
		return tbaUpdateId;
	}

	public void setTbaUpdateId(Integer tbaUpdateId) {
		this.tbaUpdateId = tbaUpdateId;
	}

	public String getTbaEmail() {
		return tbaEmail;
	}

	public void setTbaEmail(String tbaEmail) {
		this.tbaEmail = tbaEmail;
	}

	public String getTbaPassword() {
		return tbaPassword;
	}

	public void setTbaPassword(String tbaPassword) {
		this.tbaPassword = tbaPassword;
	}

	public String getTbaStatus() {
		return tbaStatus;
	}

	public void setTbaStatus(String tbaStatus) {
		this.tbaStatus = tbaStatus;
	}

	public String getTbaTokenSalt() {
		return tbaTokenSalt;
	}

	public void setTbaTokenSalt(String tbaTokenSalt) {
		this.tbaTokenSalt = tbaTokenSalt;
	}

	public String getTbaRole() {
		return tbaRole;
	}

	public void setTbaRole(String tbaRole) {
		this.tbaRole = tbaRole;
	}
}