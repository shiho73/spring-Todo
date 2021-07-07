package com.example.demo.task;

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
	private String task_name;
	private int user_id;
	private String dline;
	private int prt_num;
	private int cg_id;
	private int group_id;
	private int progress;
	private String nemo;
	private boolean tash;

	public Task() {

	}

	public Task(int code, String task_name, int user_id, String dline, int prt_num, int cg_id, int group_id,
			int progress, String nemo, boolean tash) {
		super();
		this.code = code;
		this.task_name = task_name;
		this.user_id = user_id;
		this.dline = dline;
		this.prt_num = prt_num;
		this.cg_id = cg_id;
		this.group_id = group_id;
		this.progress = progress;
		this.nemo = nemo;
		this.tash = tash;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getTask_name() {
		return task_name;
	}

	public void setTask_name(String task_name) {
		this.task_name = task_name;
	}

	public int getUser_id() {
		return user_id;
	}

	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}

	public String getDline() {
		return dline;
	}

	public void setDline(String dline) {
		this.dline = dline;
	}

	public int getPrt_num() {
		return prt_num;
	}

	public void setPrt_num(int prt_num) {
		this.prt_num = prt_num;
	}

	public int getCg_id() {
		return cg_id;
	}

	public void setCg_id(int cg_id) {
		this.cg_id = cg_id;
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

	public String getNemo() {
		return nemo;
	}

	public void setNemo(String nemo) {
		this.nemo = nemo;
	}

	public boolean isTash() {
		return tash;
	}

	public void setTash(boolean tash) {
		this.tash = tash;
	}

}
