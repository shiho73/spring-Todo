package com.example.demo.task;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="task")
public class Task {
	@Id
	@Column(name="code")
	private Integer code;

}
