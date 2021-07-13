package com.example.demo.task;

import java.sql.Date;

import javax.persistence.Column;
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

	@Column(name="user_id")
	private int userId;

	private Date dline;

	@Column(name="prt_num")
	private int prtNum;

	@Column(name="cg_code")
	private int cgCode;

	@Column(name="group_id")
	private int groupId;

	private int progress;
	private String memo;
	private boolean trash;

	public Task() {

	}

	//新規アクション関係
	public Task(String name, int user_id, Date dline, int prt_num, int cg_code, int group_id,String memo, boolean trash) {
		super();
		this.name = name;
		this.userId = user_id;
		this.dline = dline;
		this.prtNum = prt_num;
		this.cgCode = cg_code;
		this.groupId = group_id;
		this.memo = memo;
		this.trash = trash;
	}

	public Task(Integer code, String name, int user_id, Date dline, int prt_num, int cg_code, int group_id,
			int progress, String memo, boolean trash) {
		super();
		this.code = code;
		this.name = name;
		this.userId = user_id;
		this.dline = dline;
		this.prtNum = prt_num;
		this.cgCode = cg_code;
		this.groupId = group_id;
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

	//一括更新
	public Task(Integer code,int progress) {
		super();

		this.code = code;
		this.progress = progress;
	}

	//ゲッタとセッタ
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

	public int getUserId() {
		return userId;
	}

	public void setUserId(int user_id) {
		this.userId = user_id;
	}

	public Date getDline() {
		return dline;
	}

	public void setDline(Date dline) {
		this.dline = dline;
	}

	public int getPrtNum() {
		return prtNum;
	}

	public void setPrtNum(int prt_num) {
		this.prtNum = prt_num;
	}

	public int getCgCode() {
		return cgCode;
	}

	public void setCgCode(int cg_code) {
		this.cgCode = cg_code;
	}

	public int getGroupId() {
		return groupId;
	}

	public void setGroupId(int group_id) {
		this.groupId = group_id;
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