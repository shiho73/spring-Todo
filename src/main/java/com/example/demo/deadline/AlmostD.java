package com.example.demo.deadline;

import java.sql.Date;

public class AlmostD {

	private Integer code;
	private Date deadline;
	private boolean almost;

	public AlmostD(Integer code, Date deadline, boolean almost) {
		super();
		this.code = code;
		this.deadline = deadline;
		this.almost = almost;
	}

	public AlmostD() {

	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public Date getDeadline() {
		return deadline;
	}

	public void setDeadline(Date deadline) {
		this.deadline = deadline;
	}

	public boolean isAlmost() {
		return almost;
	}

	public void setAlmost(boolean almost) {
		this.almost = almost;
	}


}
