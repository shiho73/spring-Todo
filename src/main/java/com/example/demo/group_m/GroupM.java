package com.example.demo.group_m;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="t_group_m")
public class GroupM {

	@Id
	@Column(name="group_id")
	private Integer groupId;
	private String member;

	public GroupM() {

	}

	public GroupM(Integer groupId, String member) {
		super();
		this.groupId = groupId;
		this.member = member;
	}

	public Integer getGroupId() {
		return groupId;
	}

	public void setGroupId(Integer groupId) {
		this.groupId = groupId;
	}

	public String getMember() {
		return member;
	}

	public void setMember(String member) {
		this.member = member;
	}

}
