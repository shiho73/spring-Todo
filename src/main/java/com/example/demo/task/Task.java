package com.example.demo.task;

import java.sql.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "m_task")
public class Task {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer code;
	private String name;
	private int user_id;
	private Date dline;
	private int prt_num;
	private int cg_code;
	private int group_id;
	private int progress;
	private String memo;
	private boolean trash;

	public Task() {

	}

	public Task(Integer code, String name, int user_id, Date dline, int prt_num, int cg_code, int group_id,
			int progress, String memo, boolean trash) {
		super();
		this.code = code;
		this.name = name;
		this.user_id = user_id;
		this.dline = dline;
		this.prt_num = prt_num;
		this.cg_code = cg_code;
		this.group_id = group_id;
		this.progress = progress;
		this.memo = memo;
		this.trash = trash;
	}

	//ゴミ箱関係
	public Task(Integer code,boolean trash) {
		super();
		this.code = code;
		this.trash = trash;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getUser_id() {
		return user_id;
	}

	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}

	public Date getDline() {
		return dline;
	}

	public void setDline(Date dline) {
		this.dline = dline;
	}

	public int getPrt_num() {
		return prt_num;
	}

	public void setPrt_num(int prt_num) {
		this.prt_num = prt_num;
	}

	public int getCg_code() {
		return cg_code;
	}

	public void setCg_code(int cg_code) {
		this.cg_code = cg_code;
	}

	public int getGroup_id() {
		return group_id;
	}

	public void setGroup_id(int group_id) {
		this.group_id = group_id;
	}

	public int getProgress() {
		return progress;
	}

	public void setProgress(int progress) {
		this.progress = progress;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public boolean isTrash() {
		return trash;
	}

	public void setTrash(boolean trash) {
		this.trash = trash;
	}




}