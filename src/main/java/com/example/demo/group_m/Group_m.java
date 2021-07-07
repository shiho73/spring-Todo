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
	private Integer code;
	private String member;

	public Group_m(Integer code, String member) {
		super();
		this.code = code;
		this.member = member;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getMember() {
		return member;
	}

	public void setMember(String member) {
		this.member = member;
	}


}
