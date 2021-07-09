package com.example.demo.group;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="t_group")
public class Group {

	@Id
	private Integer id;
	private String name;

	public Group() {

	}

	public Group(Integer id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

	public Group(String name) {
		super();
		this.name = name;
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



}
