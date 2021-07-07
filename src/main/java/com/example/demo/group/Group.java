package com.example.demo.group;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="group")
public class Group {
	@Id
	@Column(name="code")
	private Integer code;

}
