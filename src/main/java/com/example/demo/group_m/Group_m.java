package com.example.demo.group_m;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="t_group_m")
public class Group_m {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer group_id;
	private String member;


	public Group_m(Integer group_id, String member) {
		super();
		this.group_id = group_id;
		this.member = member;
	}


	public Integer getGroup_id() {
		return group_id;
	}


	public void setGroup_id(Integer group_id) {
		this.group_id = group_id;
	}


	public String getMember() {
		return member;
	}


	public void setMember(String member) {
		this.member = member;
	}



}
