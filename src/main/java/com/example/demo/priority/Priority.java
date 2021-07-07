package com.example.demo.priority;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="t_priority")
public class Priority {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer num;
	private String name;

	public Priority() {

	}

	public Priority(Integer num, String name) {
		super();
		this.num = num;
		this.name = name;
	}

	public Integer getNum() {
		return num;
	}

	public void setNum(Integer num) {
		this.num = num;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}




}
