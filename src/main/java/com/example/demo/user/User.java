package com.example.demo.user;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="t_user")
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	private String name;
	private String pw;
	private String himitu;

	@Column(name="himitu_code")
	private Integer himituCode;

	public User() {

	}

	public User(String name, String pw) {
		super();
		this.name = name;
		this.pw = pw;
	}


	public User(Integer id, String name, String pw) {
		super();
		this.id = id;
		this.name = name;
		this.pw = pw;
	}

	public User(Integer id, String name, String pw, String himitu) {
		super();
		this.id = id;
		this.name = name;
		this.pw = pw;
		this.himitu = himitu;
	}

	public User(String name, String pw, String himitu, Integer himituCode) {
		super();
		this.name = name;
		this.pw = pw;
		this.himitu = himitu;
		this.himituCode = himituCode;
	}

	public User(Integer id, String name, String pw, String himitu, Integer himituCode) {
		super();
		this.id = id;
		this.name = name;
		this.pw = pw;
		this.himitu = himitu;
		this.himituCode = himituCode;
	}

	public Integer getHimituCode() {
		return himituCode;
	}

	public void setHimituCode(Integer himituCode) {
		this.himituCode = himituCode;
	}

	public String getHimitu() {
		return himitu;
	}

	public void setHimitu(String himitu) {
		this.himitu = himitu;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPw() {
		return pw;
	}

	public void setPw(String pw) {
		this.pw = pw;
	}




}
